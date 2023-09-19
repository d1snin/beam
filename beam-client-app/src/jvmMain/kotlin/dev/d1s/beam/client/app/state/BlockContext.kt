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
import dev.d1s.beam.client.void
import dev.d1s.beam.commons.*
import dev.d1s.beam.commons.contententity.ContentEntities
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.lighthousegames.logging.logging

private val log = logging()

public class BlockContext internal constructor(
    initialBlock: Block,
    internal val client: BeamClient
) {
    private var internalBlock = initialBlock

    public val block: Block get() = internalBlock

    private val operationLock = Mutex()

    public suspend fun setIndex(index: suspend () -> BlockIndex) {
        modifyBlock(index = index())
    }

    public suspend fun setSize(size: suspend () -> BlockSize) {
        modifyBlock(size = size())
    }

    public suspend fun setEntities(entities: suspend ContentEntitiesBuilder.() -> Unit) {
        val builtEntities = ContentEntitiesBuilder().apply { entities() }.buildContentEntities()
        modifyBlock(entities = builtEntities)
    }

    public suspend fun setMetadata(metadata: suspend () -> Metadata) {
        modifyBlock(metadata = metadata())
    }

    private suspend fun modifyBlock(
        index: BlockIndex = block.index,
        size: BlockSize = block.size,
        entities: ContentEntities = block.entities,
        metadata: Metadata = block.metadata,
    ) {
        operationLock.withLock {
            val modification = BlockModification(index, size, entities, metadata, block.spaceId)

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

    log.i {
        "Creating another block for space '$space'..."
    }

    val createdBlock = client.postBlock {
        index = client.getBlocks(space).getOrThrow().size
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