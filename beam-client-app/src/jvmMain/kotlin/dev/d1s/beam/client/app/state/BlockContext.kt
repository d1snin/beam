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
import dev.d1s.beam.client.ContentEntitiesBuilder
import dev.d1s.beam.client.MetadataBuilder
import dev.d1s.beam.client.app.util.MetadataKeys
import dev.d1s.beam.client.void
import dev.d1s.beam.commons.*
import dev.d1s.beam.commons.contententity.ContentEntities
import dev.d1s.beam.commons.contententity.ContentEntityModification
import dev.d1s.beam.commons.contententity.ContentEntityModifications
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.lighthousegames.logging.logging

internal const val DEFAULT_ROW = 0

private val log = logging()

public class BlockContext internal constructor(
    initialBlock: Block,
    private val manage: Boolean,
    internal val client: BeamClient
) {
    private var internalBlock = initialBlock

    public val block: Block get() = internalBlock

    private val operationLock = Mutex()

    public suspend fun setRow(row: suspend () -> RowIndex) {
        modifyBlock(row = row())
    }

    public suspend fun setIndex(index: suspend () -> BlockIndex?) {
        modifyBlock(index = index())
    }

    public suspend fun setSize(size: suspend () -> BlockSize) {
        modifyBlock(size = size())
    }

    public suspend fun setEntities(entities: suspend ContentEntitiesBuilder.() -> Unit) {
        val builtEntities = ContentEntitiesBuilder().apply { entities() }.buildContentEntityModifications()
        modifyBlock(entities = builtEntities)
    }

    public suspend fun setMetadata(metadata: suspend MetadataBuilder.() -> Unit) {
        val builtMetadata = MetadataBuilder().apply { metadata() }.buildMetadata()
        modifyBlock(metadata = builtMetadata)
    }

    private suspend fun modifyBlock(
        row: RowIndex = block.row,
        index: BlockIndex? = block.index,
        size: BlockSize = block.size,
        entities: ContentEntityModifications = block.entities.toModifications(),
        metadata: Metadata = block.metadata,
    ) {
        operationLock.withLock {
            val validIndex =
                if (row != block.row && index == block.index) {
                    null
                } else {
                    index
                }

            val processedMetadata = metadata.toMutableMap().apply {
                if (manage) {
                    set(MetadataKeys.APP_BLOCK_MANAGED, "true")
                }
            }

            val modification = BlockModification(row, validIndex, size, entities, processedMetadata, block.spaceId)

            log.d {
                "Modifying block with the following data: $modification"
            }

            val modifiedBlock = client.putBlock(block.id, modification).getOrThrow()
            internalBlock = modifiedBlock
        }
    }
}

public suspend fun SpaceContext.block(manage: Boolean = true, configure: suspend BlockContext.() -> Unit): Block {
    val space = space.id

    log.i {
        "Creating another block for space '$space'..."
    }

    val createdBlock = client.postBlock {
        row = this@block.currentRow
        index = null
        size = BlockSize.SMALL
        spaceId = space

        if (manage) {
            metadata(MetadataKeys.APP_BLOCK_MANAGED, "true")
        }

        void()
    }.getOrThrow()

    val context = BlockContext(
        createdBlock,
        manage,
        client
    )

    context.configure()

    return context.block
}

public suspend fun SpaceContext.sizedBlock(size: BlockSize, configure: suspend BlockContext.() -> Unit): Block =
    block {
        setSize {
            size
        }

        configure()
    }

public suspend fun SpaceContext.sizedBlockWithEntities(
    size: BlockSize,
    row: RowIndex? = null,
    configureEntities: suspend ContentEntitiesBuilder.() -> Unit
): Block =
    sizedBlock(size) {
        row?.let {
            setRow { it }
        }

        setEntities(configureEntities)
    }

public suspend fun SpaceContext.smallBlock(configure: suspend BlockContext.() -> Unit): Block =
    sizedBlock(size = BlockSize.SMALL, configure)

public suspend fun SpaceContext.mediumBlock(configure: suspend BlockContext.() -> Unit): Block =
    sizedBlock(size = BlockSize.MEDIUM, configure)

public suspend fun SpaceContext.largeBlock(configure: suspend BlockContext.() -> Unit): Block =
    sizedBlock(size = BlockSize.LARGE, configure)

public suspend fun SpaceContext.extraLargeBlock(configure: suspend BlockContext.() -> Unit): Block =
    sizedBlock(size = BlockSize.EXTRA_LARGE, configure)

public suspend fun SpaceContext.smallBlockWithEntities(
    row: RowIndex? = null,
    configureEntities: suspend ContentEntitiesBuilder.() -> Unit
): Block = sizedBlockWithEntities(size = BlockSize.SMALL, row, configureEntities)

public suspend fun SpaceContext.mediumBlockWithEntities(
    row: RowIndex? = null,
    configureEntities: suspend ContentEntitiesBuilder.() -> Unit
): Block = sizedBlockWithEntities(size = BlockSize.MEDIUM, row, configureEntities)

public suspend fun SpaceContext.largeBlockWithEntities(
    row: RowIndex? = null,
    configureEntities: suspend ContentEntitiesBuilder.() -> Unit
): Block = sizedBlockWithEntities(size = BlockSize.LARGE, row, configureEntities)

public suspend fun SpaceContext.extraLargeBlockWithEntities(
    row: RowIndex? = null,
    configureEntities: suspend ContentEntitiesBuilder.() -> Unit
): Block = sizedBlockWithEntities(size = BlockSize.EXTRA_LARGE, row, configureEntities)

private fun ContentEntities.toModifications() =
    map {
        ContentEntityModification(it.type, it.parameters)
    }