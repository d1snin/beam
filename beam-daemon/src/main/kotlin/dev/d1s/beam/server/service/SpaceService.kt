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

package dev.d1s.beam.server.service

import dev.d1s.beam.commons.Role
import dev.d1s.beam.commons.Space
import dev.d1s.beam.commons.SpaceToken
import dev.d1s.beam.commons.SpaceWithToken
import dev.d1s.beam.commons.event.EntityUpdate
import dev.d1s.beam.commons.event.EventReferences
import dev.d1s.beam.server.configuration.DtoConverters
import dev.d1s.beam.server.database.SpaceRepository
import dev.d1s.beam.server.entity.SpaceEntity
import dev.d1s.beam.server.entity.asString
import dev.d1s.beam.server.entity.isRoot
import dev.d1s.beam.server.exception.ForbiddenException
import dev.d1s.beam.server.exception.UnprocessableEntityException
import dev.d1s.exkt.dto.DtoConverter
import dev.d1s.exkt.dto.ResultingEntityWithOptionalDto
import dev.d1s.exkt.dto.convertToDtoIf
import dev.d1s.exkt.ktor.server.postgres.handlePsqlUniqueViolationThrowingConflictStatusException
import dev.d1s.exkt.ktorm.dto.ResultingExportedSequenceWithOptionalDto
import dev.d1s.exkt.ktorm.dto.convertExportedSequenceToDtoIf
import dev.d1s.ktor.events.commons.event
import dev.d1s.ktor.events.server.WebSocketEventChannel
import io.ktor.server.plugins.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import java.util.*

internal interface SpaceService {

    suspend fun createSpace(
        space: SpaceEntity,
        allowRootCreation: Boolean = false
    ): ResultingEntityWithOptionalDto<SpaceEntity, SpaceWithToken>

    suspend fun createRootSpace(): ResultingEntityWithOptionalDto<SpaceEntity, SpaceWithToken>

    suspend fun getSpace(
        uniqueIdentifier: String,
        requireDto: Boolean = false
    ): ResultingEntityWithOptionalDto<SpaceEntity, Space>

    suspend fun getSpaces(
        limit: Int,
        offset: Int,
        requireDto: Boolean = false
    ): ResultingExportedSequenceWithOptionalDto<SpaceEntity, Space>

    suspend fun updateSpace(
        uniqueIdentifier: String,
        modification: SpaceEntity
    ): ResultingEntityWithOptionalDto<SpaceEntity, Space>

    suspend fun removeSpace(uniqueIdentifier: String): Result<Unit>
}

internal class DefaultSpaceService : SpaceService, KoinComponent {

    private val spaceRepository by inject<SpaceRepository>()

    private val spaceDtoConverter by inject<DtoConverter<SpaceEntity, Space>>(qualifier = DtoConverters.SpaceDtoConverterQualifier)

    private val eventChannel by inject<WebSocketEventChannel>()

    private val authService by inject<AuthService>()

    private val logger = logging()
    override suspend fun createSpace(
        space: SpaceEntity,
        allowRootCreation: Boolean
    ): ResultingEntityWithOptionalDto<SpaceEntity, SpaceWithToken> =
        runCatching {
            logger.d {
                "Creating space ${space.asString}"
            }

            if (space.slug == SpaceEntity.ROOT_SPACE_SLUG && !allowRootCreation) {
                logger.e {
                    "Root creation is not allowed"
                }

                throw ForbiddenException()
            }

            val addedSpace = handlePsqlUniqueViolationThrowingConflictStatusException {
                spaceRepository.addSpace(space).getOrThrow()
            }

            val addedSpaceDto = spaceDtoConverter.convertToDto(addedSpace)
            sendSpaceCreatedEvent(addedSpaceDto)

            val token = authService.createToken(space)

            addedSpace to addedSpace.toSpaceWithToken(token)
        }

    override suspend fun createRootSpace(): ResultingEntityWithOptionalDto<SpaceEntity, SpaceWithToken> =
        runCatching {
            logger.d {
                "Creating root space..."
            }

            val rootSpace = SpaceEntity {
                slug = SpaceEntity.ROOT_SPACE_SLUG
                role = Role.ROOT
            }

            createSpace(rootSpace).getOrThrow()
        }

    override suspend fun getSpace(
        uniqueIdentifier: String,
        requireDto: Boolean
    ): ResultingEntityWithOptionalDto<SpaceEntity, Space> =
        runCatching {
            logger.d {
                "Obtaining space with unique identifier $uniqueIdentifier..."
            }

            val uuid = runCatching {
                UUID.fromString(uniqueIdentifier)
            }.getOrNull()

            val space = uuid?.let {
                spaceRepository.findSpaceById(it).getOrNull()
            } ?: spaceRepository.findSpaceBySlug(uniqueIdentifier).getOrElse {
                throw NotFoundException(it.message)
            }

            space to spaceDtoConverter.convertToDtoIf(space) {
                requireDto
            }
        }

    override suspend fun getSpaces(
        limit: Int,
        offset: Int,
        requireDto: Boolean
    ): ResultingExportedSequenceWithOptionalDto<SpaceEntity, Space> =
        runCatching {
            logger.d {
                "Obtaining spaces with limit $limit and offset $offset..."
            }

            val spaces = spaceRepository.findAllSpaces(limit, offset).getOrThrow()

            spaces to spaceDtoConverter.convertExportedSequenceToDtoIf(spaces) {
                requireDto
            }
        }

    override suspend fun updateSpace(
        uniqueIdentifier: String,
        modification: SpaceEntity
    ): ResultingEntityWithOptionalDto<SpaceEntity, Space> =
        runCatching {
            logger.d {
                "Updating space with unique identifier $uniqueIdentifier with data ${modification.asString}..."
            }

            val (originalSpace, originalSpaceDto) = getSpace(uniqueIdentifier, requireDto = true).getOrThrow()
            requireNotNull(originalSpaceDto)

            if (originalSpace.isRoot) {
                logger.d {
                    "Space ${originalSpace.id} is root. Unable to remove it."
                }

                throw UnprocessableEntityException()
            }

            originalSpace.apply {
                this.slug = modification.slug
            }

            val updatedSpace = handlePsqlUniqueViolationThrowingConflictStatusException {
                spaceRepository.updateSpace(originalSpace).getOrThrow()
            }

            val updatedSpaceDto = spaceDtoConverter.convertToDto(updatedSpace)

            sendSpaceUpdatedEvent(originalSpaceDto, updatedSpaceDto)

            updatedSpace to updatedSpaceDto
        }

    override suspend fun removeSpace(uniqueIdentifier: String): Result<Unit> =
        runCatching {
            logger.d {
                "Removing space with unique identifier $uniqueIdentifier..."
            }

            val (space, spaceDto) = getSpace(uniqueIdentifier, requireDto = true).getOrThrow()
            requireNotNull(spaceDto)

            if (space.isRoot) {
                logger.d {
                    "Space ${space.id} is root. Unable to remove it."
                }

                throw UnprocessableEntityException()
            }

            spaceRepository.removeSpace(space).getOrThrow()

            sendSpaceRemovedEvent(spaceDto)
        }

    private suspend fun sendSpaceCreatedEvent(spaceDto: Space) {
        val event = event(EventReferences.spaceCreated, spaceDto)
        eventChannel.send(event)
    }

    private suspend fun sendSpaceUpdatedEvent(oldSpaceDto: Space, newSpaceDto: Space) {
        val eventRef = EventReferences.spaceUpdated(newSpaceDto.id)
        val update = EntityUpdate(oldSpaceDto, newSpaceDto)
        val event = event(eventRef, update)
        eventChannel.send(event)
    }

    private suspend fun sendSpaceRemovedEvent(spaceDto: Space) {
        val eventRef = EventReferences.spaceRemoved(spaceDto.id)
        val event = event(eventRef, spaceDto)
        eventChannel.send(event)
    }

    private fun SpaceEntity.toSpaceWithToken(token: SpaceToken) =
        SpaceWithToken(
            id.toString(),
            slug,
            role,
            token
        )
}