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
import dev.d1s.beam.daemon.entity.RowEntities
import dev.d1s.beam.daemon.entity.RowEntity
import dev.d1s.beam.daemon.entity.SpaceEntity
import dev.d1s.beam.daemon.util.withIoCatching
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.*

interface RowRepository {

    suspend fun addRow(row: RowEntity): Result<RowEntity>

    suspend fun findRow(index: RowIndex, space: SpaceEntity): Result<RowEntity>

    suspend fun findRows(space: SpaceEntity): Result<RowEntities>

    suspend fun updateRow(row: RowEntity): Result<RowEntity>
}

class DefaultRowRepository : RowRepository, KoinComponent {

    private val database by inject<Database>()

    override suspend fun addRow(row: RowEntity): Result<RowEntity> =
        withIoCatching {
            row.apply {
                setId()
                database.rows.add(this)
            }
        }

    override suspend fun findRow(index: RowIndex, space: SpaceEntity): Result<RowEntity> =
        withIoCatching {
            val spaceId = space.id

            database.rows.find {
                (it.index eq index) and (it.spaceId eq spaceId)
            } ?: error("Row not found with index $index in space '$spaceId'")
        }

    override suspend fun findRows(space: SpaceEntity): Result<RowEntities> =
        withIoCatching {
            database.rows.filter {
                it.spaceId eq space.id
            }.toList()
        }

    override suspend fun updateRow(row: RowEntity): Result<RowEntity> =
        withIoCatching {
            row.apply {
                flushChanges()
            }
        }
}