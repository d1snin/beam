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

package dev.d1s.beam.daemon.service

import dev.d1s.beam.commons.*
import dev.d1s.beam.commons.event.EntityUpdate
import dev.d1s.beam.commons.event.EventReferences
import dev.d1s.beam.daemon.configuration.DtoConverters
import dev.d1s.beam.daemon.database.SpaceRepository
import dev.d1s.beam.daemon.entity.SpaceEntity
import dev.d1s.beam.daemon.entity.asString
import dev.d1s.beam.daemon.entity.isRoot
import dev.d1s.beam.daemon.exception.ForbiddenException
import dev.d1s.beam.daemon.exception.UnprocessableEntityException
import dev.d1s.exkt.dto.DtoConverter
import dev.d1s.exkt.dto.ResultingEntityWithOptionalDto
import dev.d1s.exkt.dto.convertToDtoIf
import dev.d1s.exkt.ktor.server.postgres.handlePsqlUniqueViolationThrowingConflictStatusException
import dev.d1s.exkt.ktorm.ExportedSequence
import dev.d1s.exkt.ktorm.dto.ResultingExportedSequenceWithOptionalDto
import dev.d1s.exkt.ktorm.dto.convertExportedSequenceToDtoIf
import dev.d1s.ktor.events.server.WebSocketEventChannel
import dev.d1s.ktor.events.server.event
import io.ktor.server.plugins.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import java.util.*

interface SpaceService {

    suspend fun createSpace(
        space: SpaceEntity,
        allowRootCreation: Boolean = false,
        languageCode: LanguageCode? = null
    ): ResultingEntityWithOptionalDto<SpaceEntity, Space>

    suspend fun createRootSpace(
        space: SpaceEntity,
        languageCode: LanguageCode? = null
    ): ResultingEntityWithOptionalDto<SpaceEntity, Space>

    suspend fun getSpace(
        uniqueIdentifier: SpaceIdentifier,
        languageCode: LanguageCode? = null,
        requireDto: Boolean = false
    ): ResultingEntityWithOptionalDto<SpaceEntity, Space>

    suspend fun getSpaces(
        limit: Int,
        offset: Int,
        languageCode: LanguageCode? = null,
        requireDto: Boolean = false
    ): ResultingExportedSequenceWithOptionalDto<SpaceEntity, Space>

    suspend fun updateSpace(
        uniqueIdentifier: SpaceIdentifier,
        modification: SpaceEntity,
        languageCode: LanguageCode? = null
    ): ResultingEntityWithOptionalDto<SpaceEntity, Space>

    suspend fun updateRootSpace(
        modification: SpaceEntity,
        languageCode: LanguageCode? = null
    ): ResultingEntityWithOptionalDto<SpaceEntity, Space>

    suspend fun removeSpace(uniqueIdentifier: SpaceIdentifier): Result<Unit>
}

class DefaultSpaceService : SpaceService, KoinComponent {

    private val spaceRepository by inject<SpaceRepository>()

    private val spaceDtoConverter by inject<DtoConverter<SpaceEntity, Space>>(DtoConverters.SpaceDtoConverterQualifier)

    private val eventChannel by inject<WebSocketEventChannel>()

    private val translationService by inject<TranslationService>()

    private val logger = logging()
    override suspend fun createSpace(
        space: SpaceEntity,
        allowRootCreation: Boolean,
        languageCode: LanguageCode?
    ): ResultingEntityWithOptionalDto<SpaceEntity, Space> =
        spaceRepository.withTransactionCatching {
            logger.d {
                "Creating space ${space.asString}..."
            }

            checkRootCreation(space, allowRootCreation)
            checkRootSpaceCreated(space)

            if (!space.isRoot) {
                space.role = Role.DEFAULT
            }

            val addedSpace = handleUniqueSlugViolation {
                spaceRepository.addSpace(space).getOrThrow()
            }

            val translatedSpace = translateOptionally(addedSpace, languageCode)
            val translatedSpaceDto = spaceDtoConverter.convertToDto(translatedSpace)

            sendSpaceCreatedEvent(translatedSpaceDto)

            translatedSpace to translatedSpaceDto
        }

    override suspend fun createRootSpace(
        space: SpaceEntity,
        languageCode: LanguageCode?
    ): ResultingEntityWithOptionalDto<SpaceEntity, Space> =
        spaceRepository.withTransactionCatching {
            logger.d {
                "Creating root space..."
            }

            val rootSpace = SpaceEntity {
                slug = ROOT_SPACE_SLUG
                metadata = space.metadata
                view = space.view
                role = Role.ROOT
            }

            createSpace(rootSpace, allowRootCreation = true, languageCode).getOrThrow()
        }

    override suspend fun getSpace(
        uniqueIdentifier: SpaceIdentifier,
        languageCode: LanguageCode?,
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
                throw NotFoundException("Space not found")
            }

            val translatedSpace = translateOptionally(space, languageCode)

            translatedSpace to spaceDtoConverter.convertToDtoIf(translatedSpace) {
                requireDto
            }
        }

    override suspend fun getSpaces(
        limit: Int,
        offset: Int,
        languageCode: LanguageCode?,
        requireDto: Boolean
    ): ResultingExportedSequenceWithOptionalDto<SpaceEntity, Space> =
        runCatching {
            logger.d {
                "Obtaining spaces with limit $limit and offset $offset..."
            }

            val spaces = spaceRepository.findAllSpaces(limit, offset).getOrThrow()

            val translatedSpaces = translateOptionally(spaces, languageCode)

            translatedSpaces to spaceDtoConverter.convertExportedSequenceToDtoIf(translatedSpaces) {
                requireDto
            }
        }

    override suspend fun updateSpace(
        uniqueIdentifier: SpaceIdentifier,
        modification: SpaceEntity,
        languageCode: LanguageCode?
    ): ResultingEntityWithOptionalDto<SpaceEntity, Space> =
        spaceRepository.withTransactionCatching {
            logger.d {
                "Updating space with unique identifier $uniqueIdentifier with data ${modification.asString}..."
            }

            val (originalSpace, originalSpaceDto) = getSpace(uniqueIdentifier, requireDto = true).getOrThrow()
            requireNotNull(originalSpaceDto)

            checkRootSpaceModification(originalSpace)

            originalSpace.apply {
                this.slug = modification.slug
                this.metadata = modification.metadata
                this.view = modification.view
            }

            val updatedSpace = handleUniqueSlugViolation {
                spaceRepository.updateSpace(originalSpace).getOrThrow()
            }
            val translatedSpace = translateOptionally(updatedSpace, languageCode)
            val translatedSpaceDto = spaceDtoConverter.convertToDto(translatedSpace)

            sendSpaceUpdatedEvent(originalSpaceDto, translatedSpaceDto)

            translatedSpace to translatedSpaceDto
        }

    override suspend fun updateRootSpace(
        modification: SpaceEntity,
        languageCode: LanguageCode?
    ): ResultingEntityWithOptionalDto<SpaceEntity, Space> =
        spaceRepository.withTransactionCatching {
            logger.d {
                "Updating root space with data ${modification.asString}..."
            }

            checkRootSpaceCreated()

            val (originalSpace, originalSpaceDto) = getSpace(
                ROOT_SPACE_SLUG,
                requireDto = true
            ).getOrThrow()
            requireNotNull(originalSpaceDto)

            originalSpace.apply {
                this.metadata = modification.metadata
                this.view = modification.view
            }

            val updatedSpace = spaceRepository.updateSpace(originalSpace).getOrThrow()
            val translatedSpace = translateOptionally(updatedSpace, languageCode)
            val translatedSpaceDto = spaceDtoConverter.convertToDto(translatedSpace)

            sendSpaceUpdatedEvent(originalSpaceDto, translatedSpaceDto)

            translatedSpace to translatedSpaceDto
        }

    override suspend fun removeSpace(uniqueIdentifier: SpaceIdentifier): Result<Unit> =
        spaceRepository.withTransactionCatching {
            logger.d {
                "Removing space with unique identifier $uniqueIdentifier..."
            }

            val (space, spaceDto) = getSpace(uniqueIdentifier, requireDto = true).getOrThrow()
            requireNotNull(spaceDto)

            checkRootSpaceModification(space)

            spaceRepository.removeSpace(space).getOrThrow()

            sendSpaceRemovedEvent(spaceDto)
        }

    private inline fun <R> handleUniqueSlugViolation(block: () -> R) =
        handlePsqlUniqueViolationThrowingConflictStatusException("Space already exists") {
            block()
        }

    private fun checkRootCreation(space: SpaceEntity, allowRootCreation: Boolean) {
        if (space.isRoot && !allowRootCreation) {
            logger.d {
                "Root creation is not allowed"
            }

            throw ForbiddenException("Unable to create root space")
        }
    }

    private suspend fun DefaultSpaceService.checkRootSpaceCreated(space: SpaceEntity? = null) {
        if (space?.isRoot == false) {
            getSpace(ROOT_SPACE_SLUG).getOrNull()
                ?: throw UnprocessableEntityException("Root space is not created")
        }
    }

    private fun checkRootSpaceModification(originalSpace: SpaceEntity) {
        if (originalSpace.isRoot) {
            logger.d {
                "Space ${originalSpace.id} is root. Unable to modify it."
            }

            throw UnprocessableEntityException("Unable to modify root space")
        }
    }

    private suspend fun translateOptionally(space: SpaceEntity, languageCode: LanguageCode?) =
        space.apply {
            languageCode?.let {
                translationService.translateSpace(space = this, languageCode = it)
            }
        }

    private suspend fun translateOptionally(spaces: ExportedSequence<SpaceEntity>, languageCode: LanguageCode?) =
        spaces.copy(
            elements = spaces.elements.map { space ->
                translateOptionally(space, languageCode)
            }
        )


    private suspend fun sendSpaceCreatedEvent(spaceDto: Space) {
        val event = event(EventReferences.spaceCreated) {
            spaceDto
        }
        eventChannel.send(event)
    }

    private suspend fun sendSpaceUpdatedEvent(oldSpaceDto: Space, newSpaceDto: Space) {
        val eventRef = EventReferences.spaceUpdated(newSpaceDto.id)
        val update = EntityUpdate(oldSpaceDto, newSpaceDto)
        val event = event(eventRef) {
            update
        }
        eventChannel.send(event)
    }

    private suspend fun sendSpaceRemovedEvent(spaceDto: Space) {
        val eventRef = EventReferences.spaceRemoved(spaceDto.id)
        val event = event(eventRef) {
            spaceDto
        }
        eventChannel.send(event)
    }
}