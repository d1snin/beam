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

package dev.d1s.beam.daemon.database

import dev.d1s.beam.commons.RowIndex
import dev.d1s.beam.daemon.entity.BlockEntities
import dev.d1s.beam.daemon.entity.BlockEntity
import dev.d1s.beam.daemon.entity.SpaceEntity
import dev.d1s.beam.daemon.entity.requiredIndex
import dev.d1s.beam.daemon.util.withIoCatching
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.greaterEq
import org.ktorm.entity.*
import java.util.*

interface BlockRepository {

    suspend fun addBlock(block: BlockEntity): Result<BlockEntity>

    suspend fun findBlockById(id: UUID): Result<BlockEntity>

    suspend fun countBlocksInSpace(space: SpaceEntity): Result<Int>

    suspend fun findBlocksInSpace(space: SpaceEntity): Result<BlockEntities>

    suspend fun findLatestBlockIndexInSpaceByRow(space: SpaceEntity, row: RowIndex): Result<Int>

    suspend fun findBlocksInSpaceByRowWhichIndexIsGreaterOrEqualTo(
        space: SpaceEntity,
        row: RowIndex,
        index: Int
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
            } ?: error("Block not found by ID $id")
        }

    override suspend fun countBlocksInSpace(space: SpaceEntity): Result<Int> =
        withIoCatching {
            findBlocksInSpaceAsSequence(space).count()
        }

    override suspend fun findBlocksInSpace(space: SpaceEntity): Result<BlockEntities> =
        withIoCatching {
            findBlocksInSpaceAsSequence(space).toList()
        }

    override suspend fun findLatestBlockIndexInSpaceByRow(space: SpaceEntity, row: RowIndex): Result<Int> =
        withIoCatching {
           findBlocksInSpaceAsSequence(space, row).sortedByDescending {
                it.index
            }.first().requiredIndex
        }

    override suspend fun findBlocksInSpaceByRowWhichIndexIsGreaterOrEqualTo(
        space: SpaceEntity,
        row: RowIndex,
        index: Int
    ): Result<BlockEntities> =
        withIoCatching {
            findBlocksInSpaceAsSequence(space, row).sortedByDescending {
                it.index
            }.filter {
                it.index greaterEq index
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

    private fun findBlocksInSpaceAsSequence(space: SpaceEntity, row: RowIndex? = null) =
        database.blocks.filter { block ->
            (block.spaceId eq space.id).let {
                if (row != null) {
                    it and (block.row eq row)
                } else {
                    it
                }
            }
        }
}