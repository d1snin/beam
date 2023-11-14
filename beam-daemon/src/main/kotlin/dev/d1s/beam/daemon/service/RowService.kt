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

package dev.d1s.beam.daemon.service

import dev.d1s.beam.commons.*
import dev.d1s.beam.commons.event.EntityUpdate
import dev.d1s.beam.commons.event.EventReferences
import dev.d1s.beam.daemon.configuration.DtoConverters
import dev.d1s.beam.daemon.database.RowRepository
import dev.d1s.beam.daemon.entity.RowEntity
import dev.d1s.beam.daemon.entity.SpaceEntity
import dev.d1s.beam.daemon.entity.asString
import dev.d1s.exkt.dto.*
import dev.d1s.ktor.events.server.WebSocketEventChannel
import dev.d1s.ktor.events.server.event
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging

interface RowService {

    suspend fun createRowIfDoesntExist(
        index: RowIndex,
        space: SpaceEntity
    ): ResultingEntityWithOptionalDto<RowEntity, Row>

    suspend fun getRow(
        index: RowIndex,
        spaceIdentifier: SpaceIdentifier,
        requireDto: Boolean = false
    ): ResultingEntityWithOptionalDto<RowEntity, Row>

    suspend fun getRows(
        spaceIdentifier: SpaceIdentifier,
        requireDto: Boolean = false
    ): ResultingEntityWithOptionalDtoList<RowEntity, Row>

    suspend fun updateRow(
        index: RowIndex,
        spaceIdentifier: SpaceIdentifier,
        row: RowEntity
    ): ResultingEntityWithOptionalDto<RowEntity, Row>
}

class DefaultRowService : RowService, KoinComponent {

    private val rowRepository by inject<RowRepository>()

    private val rowDtoConverter by inject<DtoConverter<RowEntity, Row>>(DtoConverters.RowDtoConverterQualifier)

    private val eventChannel by inject<WebSocketEventChannel>()

    private val spaceService by inject<SpaceService>()

    private val logger = logging()

    override suspend fun createRowIfDoesntExist(
        index: RowIndex,
        space: SpaceEntity
    ): ResultingEntityWithOptionalDto<RowEntity, Row> =
        runCatching {
            val spaceId = space.id.toString()

            logger.d {
                "Creating row with index $index in space $spaceId..."
            }

            val existingRow = getRow(index, spaceId, requireDto = true).getOrNull()

            if (existingRow == null) {
                val row = RowEntity {
                    this.index = index
                    this.align = RowAlign.CENTER
                    this.metadata = metadataOf()
                    this.space = space
                }

                val addedRow = rowRepository.addRow(row).getOrThrow()
                val rowDto = rowDtoConverter.convertToDto(addedRow)

                sendRowCreatedEvent(rowDto)

                addedRow to rowDto
            } else {
                logger.d {
                    "Row already exists: ${existingRow.entity.asString}"
                }

                existingRow
            }
        }

    override suspend fun getRow(
        index: RowIndex,
        spaceIdentifier: SpaceIdentifier,
        requireDto: Boolean
    ): ResultingEntityWithOptionalDto<RowEntity, Row> =
        runCatching {
            logger.d {
                "Obtaining row with index $index in space '$spaceIdentifier'..."
            }

            val space = spaceService.getSpace(spaceIdentifier).getOrThrow().entity

            val row = rowRepository.findRow(index, space).getOrThrow()

            row to rowDtoConverter.convertToDtoIf(row) {
                requireDto
            }
        }

    override suspend fun getRows(
        spaceIdentifier: SpaceIdentifier,
        requireDto: Boolean
    ): ResultingEntityWithOptionalDtoList<RowEntity, Row> =
        runCatching {
            logger.d {
                "Obtaining rows for space '$spaceIdentifier'"
            }

            val (space, _) = spaceService.getSpace(spaceIdentifier).getOrThrow()

            val rows = rowRepository.findRows(space).getOrThrow()

            rows to rowDtoConverter.convertToDtoListIf(rows) {
                requireDto
            }
        }

    override suspend fun updateRow(
        index: RowIndex,
        spaceIdentifier: SpaceIdentifier,
        row: RowEntity
    ): ResultingEntityWithOptionalDto<RowEntity, Row> =
        runCatching {
            logger.d {
                "Updating row with index $index in space '$spaceIdentifier'"
            }

            val (originalRow, originalRowDto) = getRow(index, spaceIdentifier, requireDto = true).getOrThrow()
            requireNotNull(originalRowDto)

            originalRow.apply {
                this.align = row.align
                this.metadata = row.metadata
            }

            val updatedRow = rowRepository.updateRow(originalRow).getOrThrow()
            val rowDto = rowDtoConverter.convertToDto(updatedRow)

            sendRowUpdatedEvent(originalRowDto, rowDto)

            updatedRow to rowDto
        }

    private suspend fun sendRowCreatedEvent(rowDto: Row) {
        val event = event(EventReferences.rowCreated) {
            rowDto
        }
        eventChannel.send(event)
    }

    private suspend fun sendRowUpdatedEvent(oldRowDto: Row, newRowDto: Row) {
        val qualifier = newRowDto.qualifier()
        val eventRef = EventReferences.rowUpdated(qualifier)
        val update = EntityUpdate(oldRowDto, newRowDto)
        val event = event(eventRef) {
            update
        }
        eventChannel.send(event)
    }

    private fun Row.qualifier() =
        RowQualifier(spaceId, index)
}