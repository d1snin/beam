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

import dev.d1s.beam.commons.SpaceSlug
import dev.d1s.beam.server.entity.SpaceEntity
import dev.d1s.exkt.ktorm.ExportedSequence
import dev.d1s.exkt.ktorm.export
import dispatch.core.withIO
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database
import org.ktorm.dsl.desc
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import java.util.*

internal interface SpaceRepository {

    suspend fun addSpace(space: SpaceEntity): Result<SpaceEntity>

    suspend fun findSpaceById(id: UUID): Result<SpaceEntity>

    suspend fun findSpaceBySlug(slug: SpaceSlug): Result<SpaceEntity>

    suspend fun findAllSpaces(limit: Int, offset: Int): Result<ExportedSequence<SpaceEntity>>

    suspend fun updateSpace(space: SpaceEntity): Result<SpaceEntity>

    suspend fun removeSpace(space: SpaceEntity): Result<Unit>
}

internal class DefaultSpaceRepository : SpaceRepository, KoinComponent {

    private val database by inject<Database>()

    override suspend fun addSpace(space: SpaceEntity): Result<SpaceEntity> =
        withIO {
            runCatching {
                space.apply {
                    setId()
                    setCreatedAt()
                    setUpdatedAt()
                    database.spaces.add(space)
                }
            }
        }

    override suspend fun findSpaceById(id: UUID): Result<SpaceEntity> =
        withIO {
            runCatching {
                database.spaces.find {
                    it.id eq id
                } ?: error("Space not found by ID $id")
            }
        }

    override suspend fun findSpaceBySlug(slug: SpaceSlug): Result<SpaceEntity> =
        withIO {
            runCatching {
                database.spaces.find {
                    it.slug eq slug
                } ?: error("Space not found by slug $slug")
            }
        }

    override suspend fun findAllSpaces(limit: Int, offset: Int): Result<ExportedSequence<SpaceEntity>> =
        withIO {
            runCatching {
                database.spaces.export(limit, offset, sort = { it.createdAt.desc() })
            }
        }

    override suspend fun updateSpace(space: SpaceEntity): Result<SpaceEntity> =
        withIO {
            runCatching {
                space.apply {
                    setUpdatedAt()
                    flushChanges()
                }
            }
        }

    override suspend fun removeSpace(space: SpaceEntity): Result<Unit> =
        withIO {
            runCatching {
                space.delete()
                Unit
            }
        }
}