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

package dev.d1s.beam.server.database

import dev.d1s.beam.server.entity.BlockEntities
import dev.d1s.beam.server.entity.BlockEntity
import dev.d1s.beam.server.entity.SpaceEntity
import dispatch.core.withIO
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.greaterEq
import org.ktorm.entity.*
import java.util.*

internal interface BlockRepository {

    suspend fun addBlock(block: BlockEntity): Result<BlockEntity>

    suspend fun findBlockById(id: UUID): Result<BlockEntity>

    suspend fun countBlocksInSpace(space: SpaceEntity): Result<Int>

    suspend fun findLatestBlockIndexInSpace(space: SpaceEntity): Result<Int>

    suspend fun findBlocksInSpaceWhichIndexIsGreaterOrEqualTo(space: SpaceEntity, index: Int): Result<BlockEntities>

    suspend fun findBlocksInSpace(space: SpaceEntity): Result<BlockEntities>

    suspend fun updateBlock(block: BlockEntity): Result<BlockEntity>

    suspend fun updateBlocks(blocks: BlockEntities): Result<BlockEntities>

    suspend fun removeBlock(block: BlockEntity): Result<Unit>
}

internal class DefaultBlockRepository : BlockRepository, KoinComponent {

    private val database by inject<Database>()

    override suspend fun addBlock(block: BlockEntity): Result<BlockEntity> =
        withIO {
            runCatching {
                block.apply {
                    setId()
                    database.blocks.add(block)
                }
            }
        }

    override suspend fun findBlockById(id: UUID): Result<BlockEntity> =
        withIO {
            runCatching {
                database.blocks.find {
                    it.id eq id
                } ?: error("Block not found by ID $id")
            }
        }

    override suspend fun countBlocksInSpace(space: SpaceEntity): Result<Int> =
        withIO {
            runCatching {
                findBlocksInSpaceAsSequence(space).count()
            }
        }

    override suspend fun findLatestBlockIndexInSpace(space: SpaceEntity): Result<Int> =
        withIO {
            runCatching {
                findBlocksInSpaceAsSequence(space).sortedByDescending {
                    it.index
                }.first().index
            }
        }

    override suspend fun findBlocksInSpaceWhichIndexIsGreaterOrEqualTo(
        space: SpaceEntity,
        index: Int
    ): Result<BlockEntities> =
        withIO {
            runCatching {
                findBlocksInSpaceAsSequence(space).filter {
                    it.index greaterEq index
                }.toList()
            }
        }

    override suspend fun findBlocksInSpace(space: SpaceEntity): Result<BlockEntities> =
        withIO {
            runCatching {
                findBlocksInSpaceAsSequence(space).toList()
            }
        }

    override suspend fun updateBlock(block: BlockEntity): Result<BlockEntity> =
        withIO {
            runCatching {
                block.apply {
                    flushChanges()
                }
            }
        }

    override suspend fun updateBlocks(blocks: BlockEntities): Result<BlockEntities> =
        withIO {
            runCatching {
                blocks.map {
                    it.flushChanges()
                    it
                }
            }
        }

    override suspend fun removeBlock(block: BlockEntity): Result<Unit> =
        withIO {
            runCatching {
                block.delete()
                Unit
            }
        }

    private fun findBlocksInSpaceAsSequence(space: SpaceEntity) =
        database.blocks.filter {
            it.spaceId eq space.id
        }
}