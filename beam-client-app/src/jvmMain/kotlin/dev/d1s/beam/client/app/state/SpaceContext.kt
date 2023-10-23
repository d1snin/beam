/*
 * Copyright 2023 Mikhail Titov
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
import dev.d1s.beam.client.SpaceViewBuilder
import dev.d1s.beam.client.app.ApplicationContext
import dev.d1s.beam.commons.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.lighthousegames.logging.logging

private val log = logging()

private const val DEFAULT_THEME = "catppuccin-mocha"

public class SpaceContext internal constructor(
    initialSpace: Space,
    internal val processBlocks: Boolean,
    internal val client: BeamClient
) {
    private var internalSpace = initialSpace

    public val space: Space get() = internalSpace

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
    processBlocks: Boolean = true,
    configure: suspend SpaceContext.() -> Unit
) {
    var space = client.getSpace(spaceIdentifier).getOrNull()

    if (space == null) {
        log.i {
            "Couldn't access space '$spaceIdentifier'. Creating it..."
        }

        if (spaceIdentifier == "root") {
            space = postRootSpace {
                view {
                    theme = DEFAULT_THEME
                }
            }.getOrThrow().toSpace()
        } else {
            space = postSpace {
                slug = spaceIdentifier

                view {
                    theme = DEFAULT_THEME
                }
            }.getOrThrow().toSpace()
        }
    }

    val spaceId = space.id

    log.i {
        "Accessed space `${space.slug}` ($spaceId). Cleaning it up..."
    }

    if (processBlocks) {
        val blocks = getBlocks(space.id).getOrThrow()
        blocks.forEach {
            deleteBlock(it.id).getOrThrow()
        }
    }

    log.i {
        "Space initialized."
    }

    val context = SpaceContext(space, processBlocks, client)

    context.configure()
}