/*
 * Copyright 2023-2024 Mikhail Titov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.d1s.beam.client.app.state

import dev.d1s.beam.client.BeamClient
import dev.d1s.beam.client.MetadataBuilder
import dev.d1s.beam.client.RowModificationBuilder
import dev.d1s.beam.client.SpaceViewBuilder
import dev.d1s.beam.client.app.ApplicationConfig
import dev.d1s.beam.client.app.ApplicationContext
import dev.d1s.beam.client.app.util.MetadataKeys
import dev.d1s.beam.commons.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.lighthousegames.logging.logging

private const val DEFAULT_THEME = "catppuccin-mocha"

private val log = logging()

public open class SpaceContext internal constructor(
    initialSpace: Space,
    internal val client: BeamClient,
    public val config: ApplicationConfig
) {
    private var internalSpace = initialSpace

    public val space: Space get() = internalSpace

    internal var currentRow: RowIndex = DEFAULT_ROW

    private val operationLock = Mutex()

    public suspend fun setSlug(slug: suspend () -> SpaceSlug) {
        modifySpace(slug = slug())
    }

    public suspend fun setMetadata(metadata: suspend MetadataBuilder.() -> Unit) {
        val builtMetadata = MetadataBuilder().apply { metadata() }.buildMetadata()
        modifySpace(metadata = builtMetadata)
    }

    public suspend fun setView(view: suspend SpaceViewBuilder.() -> Unit) {
        val builtView = SpaceViewBuilder().apply { view() }.buildSpaceView()
        modifySpace(view = builtView)
    }

    public suspend fun alignRow(rowIndex: RowIndex = currentRow, align: RowAlign) {
        setRow(rowIndex) {
            this.align = align
        }
    }

    public suspend fun setRow(rowIndex: RowIndex = currentRow, row: suspend RowModificationBuilder.() -> Unit) {
        client.putRow(rowIndex, space.id, row).getOrThrow()
    }

    private suspend fun modifySpace(
        slug: SpaceSlug = space.slug,
        metadata: Metadata = space.metadata,
        view: SpaceView = space.view
    ) {
        fun log(modification: Any) {
            log.d {
                "Modifying space with the following data: $modification"
            }
        }

        operationLock.withLock {
            internalSpace = if (space.role == Role.ROOT) {
                val modification = RootSpaceModification(metadata, view)
                log(modification)

                val modifiedSpace = client.putRootSpace(modification).getOrThrow()
                modifiedSpace
            } else {
                val modification = SpaceModification(slug, metadata, view)
                log(modification)

                val modifiedSpace = client.putSpace(space.id, modification).getOrThrow()
                modifiedSpace
            }
        }
    }
}

public suspend fun ApplicationContext.space(
    spaceIdentifier: SpaceIdentifier = ROOT_SPACE_SLUG,
    configure: suspend SpaceContext.() -> Unit
): Space {
    var space = client.getSpace(spaceIdentifier).getOrNull()

    if (space == null) {
        log.i {
            "Couldn't access space '$spaceIdentifier'. Creating it..."
        }

        if (spaceIdentifier == ROOT_SPACE_SLUG) {
            space = postRootSpace {
                view {
                    theme = DEFAULT_THEME
                }
            }.getOrThrow()
        } else {
            space = postSpace {
                slug = spaceIdentifier

                view {
                    theme = DEFAULT_THEME
                }
            }.getOrThrow()
        }
    }

    val spaceId = space.id

    log.i {
        "Accessed space `${space.slug}` ($spaceId). Cleaning it up..."
    }

    clearBlocks(spaceId, config.name)

    log.i {
        "Space initialized."
    }

    val context = SpaceContext(space, client, config)

    context.configure()

    return context.space
}

public suspend fun SpaceContext.row(
    index: RowIndex,
    align: RowAlign? = null,
    metadata: Metadata? = null,
    configure: suspend SpaceContext.() -> Unit
) {
    val context = SpaceContext(space, client, config).apply {
        currentRow = index
    }

    align?.let {
        context.alignRow(align = align)
    }

    metadata?.let { meta ->
        context.setRow {
            metadata {
                metadata(meta)
            }
        }
    }

    context.configure()
}

private suspend fun ApplicationContext.clearBlocks(spaceId: SpaceId, applicationName: String) {
    client.iterateBlocks(spaceId) { block ->
        val metadataKey = MetadataKeys.APP_BLOCK_MANAGED.format(applicationName)

        if (block.metadata[metadataKey] == "true") {
            client.deleteBlock(block.id).getOrThrow()
        }
    }
}