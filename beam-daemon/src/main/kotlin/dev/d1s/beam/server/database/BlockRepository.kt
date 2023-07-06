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

import dev.d1s.beam.commons.BlockSlug
import dev.d1s.beam.server.entity.BlockEntity
import dispatch.core.withIO
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.sortedBy
import org.ktorm.entity.toList
import java.util.*

internal interface BlockRepository {

    suspend fun addBlock(block: BlockEntity): Result<BlockEntity>

    suspend fun findBlockById(id: UUID): Result<BlockEntity>

    suspend fun findBlockBySlug(slug: BlockSlug): Result<BlockEntity>

    suspend fun findAllBlocks(): Result<List<BlockEntity>>

    suspend fun updateBlock(block: BlockEntity): Result<BlockEntity>

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

    override suspend fun findBlockBySlug(slug: BlockSlug): Result<BlockEntity> =
        withIO {
            runCatching {
                database.blocks.find {
                    it.slug eq slug
                } ?: error("Block not found by slug $slug")
            }
        }

    override suspend fun findAllBlocks(): Result<List<BlockEntity>> =
        withIO {
            runCatching {
                val sortedBlocks = database.blocks.sortedBy {
                    it.index
                }

                sortedBlocks.toList()
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

    override suspend fun removeBlock(block: BlockEntity): Result<Unit> =
        withIO {
            runCatching {
                block.delete()
                Unit
            }
        }
}