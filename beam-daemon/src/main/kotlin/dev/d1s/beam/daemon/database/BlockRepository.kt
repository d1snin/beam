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

package dev.d1s.beam.daemon.database

import dev.d1s.beam.commons.BlockIndex
import dev.d1s.beam.commons.RowIndex
import dev.d1s.beam.daemon.entity.BlockEntities
import dev.d1s.beam.daemon.entity.BlockEntity
import dev.d1s.beam.daemon.entity.SpaceEntity
import dev.d1s.beam.daemon.entity.requiredIndex
import dev.d1s.beam.daemon.util.withIoCatching
import dev.d1s.exkt.ktorm.ExportedSequence
import dev.d1s.exkt.ktorm.export
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import java.util.*

interface BlockRepository : AbstractRepository {

    suspend fun addBlock(block: BlockEntity): Result<BlockEntity>

    suspend fun findBlockById(id: UUID): Result<BlockEntity>

    suspend fun countBlocksInSpace(space: SpaceEntity): Result<Int>

    suspend fun findBlocksInSpace(space: SpaceEntity, limit: Int, offset: Int): Result<ExportedSequence<BlockEntity>>

    suspend fun findAllBlocksInSpace(space: SpaceEntity): Result<BlockEntities>

    suspend fun findLatestBlockIndexInSpaceByRow(space: SpaceEntity, row: RowIndex): Result<Int>

    suspend fun findBlocksInSpaceByRowWhichIndexIsGreater(
        space: SpaceEntity,
        row: RowIndex,
        index: BlockIndex
    ): Result<BlockEntities>

    suspend fun findBlocksInSpaceByRowWhichIndexIsGreaterOrEqualTo(
        space: SpaceEntity,
        row: RowIndex,
        index: BlockIndex
    ): Result<BlockEntities>

    suspend fun findBlocksInSpaceByRowWhichIndexIsBetweenEndExclusive(
        space: SpaceEntity,
        row: RowIndex,
        start: BlockIndex,
        endExclusive: BlockIndex
    ): Result<BlockEntities>

    suspend fun findBlocksInSpaceByRowWhichIndexIsBetweenStartExclusive(
        space: SpaceEntity,
        row: RowIndex,
        startExclusive: BlockIndex,
        end: BlockIndex
    ): Result<BlockEntities>

    suspend fun updateBlock(block: BlockEntity): Result<BlockEntity>

    suspend fun updateBlocks(blocks: BlockEntities): Result<BlockEntities>

    suspend fun removeBlock(block: BlockEntity): Result<Unit>
}

class DefaultBlockRepository : BlockRepository, KoinComponent {

    private val database by inject<Database>()

    override suspend fun addBlock(block: BlockEntity): Result<BlockEntity> =
        withIoCatching {
            block.apply {
                setId()
                database.blocks.add(block)
            }
        }

    override suspend fun findBlockById(id: UUID): Result<BlockEntity> =
        withIoCatching {
            database.blocks.find {
                it.id eq id
            } ?: error("Block not found by ID '$id'")
        }

    override suspend fun countBlocksInSpace(space: SpaceEntity): Result<Int> =
        withIoCatching {
            findBlocksInSpaceAsSequence(space, sorted = false).count()
        }

    override suspend fun findBlocksInSpace(
        space: SpaceEntity,
        limit: Int,
        offset: Int
    ): Result<ExportedSequence<BlockEntity>> =
        withIoCatching {
            findBlocksInSpaceAsSequence(space, sorted = false)
                .export(
                    limit,
                    offset,
                    clientTransform = {
                        sortedBy {
                            it.index
                        }.sortedBy {
                            it.row
                        }
                    }
                )
        }

    override suspend fun findAllBlocksInSpace(space: SpaceEntity): Result<BlockEntities> =
        withIoCatching {
            findBlocksInSpaceAsSequence(space, sorted = false).toList()
        }

    override suspend fun findLatestBlockIndexInSpaceByRow(space: SpaceEntity, row: RowIndex): Result<Int> =
        withIoCatching {
            findBlocksInSpaceAsSequence(space, row).first().requiredIndex
        }

    override suspend fun findBlocksInSpaceByRowWhichIndexIsGreater(
        space: SpaceEntity,
        row: RowIndex,
        index: BlockIndex
    ): Result<BlockEntities> =
        withIoCatching {
            findBlocksInSpaceAsSequence(space, row).filter {
                it.index greater index
            }.toList()
        }

    override suspend fun findBlocksInSpaceByRowWhichIndexIsGreaterOrEqualTo(
        space: SpaceEntity,
        row: RowIndex,
        index: BlockIndex
    ): Result<BlockEntities> =
        withIoCatching {
            findBlocksInSpaceAsSequence(space, row).filter {
                it.index greaterEq index
            }.toList()
        }

    override suspend fun findBlocksInSpaceByRowWhichIndexIsBetweenEndExclusive(
        space: SpaceEntity,
        row: RowIndex,
        start: BlockIndex,
        endExclusive: BlockIndex
    ): Result<BlockEntities> =
        withIoCatching {
            findBlocksInSpaceAsSequence(space, row).filter {
                (it.index greaterEq start) and (it.index less endExclusive)
            }.toList()
        }

    override suspend fun findBlocksInSpaceByRowWhichIndexIsBetweenStartExclusive(
        space: SpaceEntity,
        row: RowIndex,
        startExclusive: BlockIndex,
        end: BlockIndex
    ): Result<BlockEntities> =
        withIoCatching {
            findBlocksInSpaceAsSequence(space, row).filter {
                (it.index greater startExclusive) and (it.index lessEq end)
            }.toList()
        }

    override suspend fun updateBlock(block: BlockEntity): Result<BlockEntity> =
        withIoCatching {
            block.apply {
                flushChanges()
            }
        }

    override suspend fun updateBlocks(blocks: BlockEntities): Result<BlockEntities> =
        withIoCatching {
            database.useTransaction {
                blocks.map {
                    it.flushChanges()
                    it
                }
            }
        }

    override suspend fun removeBlock(block: BlockEntity): Result<Unit> =
        withIoCatching {
            block.delete()
            Unit
        }

    private fun findBlocksInSpaceAsSequence(space: SpaceEntity, row: RowIndex? = null, sorted: Boolean = true) =
        database.blocks.let {
            if (sorted) {
                it.sortedByDescending { block ->
                    block.index
                }
            } else {
                it
            }
        }.filter { block ->
            (block.spaceId eq space.id).let {
                if (row != null) {
                    it and (block.row eq row)
                } else {
                    it
                }
            }
        }
}