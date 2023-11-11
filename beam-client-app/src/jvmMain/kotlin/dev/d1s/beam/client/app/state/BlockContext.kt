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
import dev.d1s.beam.client.ContentEntitiesBuilder
import dev.d1s.beam.client.MetadataBuilder
import dev.d1s.beam.client.void
import dev.d1s.beam.commons.*
import dev.d1s.beam.commons.contententity.ContentEntities
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.lighthousegames.logging.logging

internal const val DEFAULT_ROW = 0

private val log = logging()

public class BlockContext internal constructor(
    initialBlock: Block,
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
        val builtEntities = ContentEntitiesBuilder().apply { entities() }.buildContentEntities()
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
        entities: ContentEntities = block.entities,
        metadata: Metadata = block.metadata,
    ) {
        operationLock.withLock {
            val modification = BlockModification(row, index, size, entities, metadata, block.spaceId)

            log.d {
                "Modifying block with the following data: $modification"
            }

            val modifiedBlock = client.putBlock(block.id, modification).getOrThrow()
            internalBlock = modifiedBlock
        }
    }
}

public suspend fun SpaceContext.block(configure: suspend BlockContext.() -> Unit) {
    val space = space.id

    suspend fun processBlock() {
        log.i {
            "Creating another block for space '$space'..."
        }

        val createdBlock = client.postBlock {
            row = this@block.currentRow
            index = null
            size = BlockSize.SMALL
            spaceId = space

            void()
        }.getOrThrow()

        val context = BlockContext(
            createdBlock,
            client
        )

        context.configure()
    }

    if (processBlocks) {
        processBlock()
    } else {
        log.i {
            "Won't process block for space '$space'."
        }
    }
}

public suspend fun SpaceContext.sizedBlock(size: BlockSize, configure: suspend BlockContext.() -> Unit) {
    block {
        setSize {
            size
        }

        configure()
    }
}

public suspend fun SpaceContext.sizedBlockWithEntities(
    size: BlockSize,
    row: RowIndex? = null,
    configureEntities: suspend ContentEntitiesBuilder.() -> Unit
) {
    sizedBlock(size) {
        row?.let {
            setRow { it }
        }

        setEntities(configureEntities)
    }
}

public suspend fun SpaceContext.smallBlock(configure: suspend BlockContext.() -> Unit) {
    sizedBlock(size = BlockSize.SMALL, configure)
}

public suspend fun SpaceContext.mediumBlock(configure: suspend BlockContext.() -> Unit) {
    sizedBlock(size = BlockSize.MEDIUM, configure)
}

public suspend fun SpaceContext.largeBlock(configure: suspend BlockContext.() -> Unit) {
    sizedBlock(size = BlockSize.LARGE, configure)
}

public suspend fun SpaceContext.extraLargeBlock(configure: suspend BlockContext.() -> Unit) {
    sizedBlock(size = BlockSize.EXTRA_LARGE, configure)
}

public suspend fun SpaceContext.smallBlockWithEntities(
    row: RowIndex? = null,
    configureEntities: suspend ContentEntitiesBuilder.() -> Unit
) {
    sizedBlockWithEntities(size = BlockSize.SMALL, row, configureEntities)
}

public suspend fun SpaceContext.mediumBlockWithEntities(
    row: RowIndex? = null,
    configureEntities: suspend ContentEntitiesBuilder.() -> Unit
) {
    sizedBlockWithEntities(size = BlockSize.MEDIUM, row, configureEntities)
}

public suspend fun SpaceContext.largeBlockWithEntities(
    row: RowIndex? = null,
    configureEntities: suspend ContentEntitiesBuilder.() -> Unit
) {
    sizedBlockWithEntities(size = BlockSize.LARGE, row, configureEntities)
}

public suspend fun SpaceContext.extraLargeBlockWithEntities(
    row: RowIndex? = null,
    configureEntities: suspend ContentEntitiesBuilder.() -> Unit
) {
    sizedBlockWithEntities(size = BlockSize.EXTRA_LARGE, row, configureEntities)
}