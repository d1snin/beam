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
import dev.d1s.beam.commons.contententity.*
import dev.d1s.beam.commons.event.EntityUpdate
import dev.d1s.beam.commons.event.EventReferences
import dev.d1s.beam.daemon.configuration.DtoConverters
import dev.d1s.beam.daemon.database.TranslationRepository
import dev.d1s.beam.daemon.entity.*
import dev.d1s.beam.daemon.exception.UnprocessableEntityException
import dev.d1s.beam.daemon.util.CommonLanguageCodes
import dev.d1s.beam.daemon.util.byCode
import dev.d1s.exkt.dto.*
import dev.d1s.exkt.ktor.server.statuspages.HttpStatusException
import dev.d1s.ktor.events.server.WebSocketEventChannel
import dev.d1s.ktor.events.server.event
import io.ktor.http.*
import io.ktor.server.plugins.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import java.util.*

interface TranslationService {

    suspend fun createTranslation(
        languageCode: LanguageCode,
        translation: TranslationEntity
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation>

    suspend fun getTranslation(
        languageCode: LanguageCode,
        requireDto: Boolean = false
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation>

    suspend fun resolveTranslation(
        languageCode: LanguageCode,
        requireDto: Boolean = false
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation>

    suspend fun getTranslations(
        requireDto: Boolean = false
    ): ResultingEntityWithOptionalDtoList<TranslationEntity, Translation>

    suspend fun updateTranslation(
        languageCode: LanguageCode,
        modification: TranslationEntity
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation>

    suspend fun removeTranslation(languageCode: LanguageCode): Result<Unit>

    suspend fun translateBlock(block: BlockEntity, languageCode: LanguageCode): Result<Unit>

    suspend fun translateSpace(space: SpaceEntity, languageCode: LanguageCode): Result<Unit>
}

class DefaultTranslationService : TranslationService, KoinComponent {

    private val translationRepository by inject<TranslationRepository>()

    private val translationDtoConverter by inject<DtoConverter<TranslationEntity, Translation>>(DtoConverters.TranslationDtoConverterQualifier)

    private val eventChannel by inject<WebSocketEventChannel>()

    private val logger = logging()

    override suspend fun createTranslation(
        languageCode: LanguageCode,
        translation: TranslationEntity
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation> =
        translationRepository.withTransactionCatching {
            logger.d {
                "Creating translation ${translation.asString}..."
            }

            translation.languageCode = languageCode

            verifyTranslationDoesntExist(translation)
            verifyLanguageName(translation)
            verifyDefaultTranslation(translation)

            val addedTranslation = translationRepository.addTranslation(translation)
                .getOrThrow()
                .inferLanguageName()

            val addedTranslationDto = translationDtoConverter.convertToDto(addedTranslation)
            sendTranslationCreatedEvent(addedTranslationDto)

            addedTranslation to addedTranslationDto
        }

    override suspend fun getTranslation(
        languageCode: LanguageCode,
        requireDto: Boolean
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation> =
        runCatching {
            logger.d {
                "Obtaining translation in language '$languageCode'..."
            }

            val translation = translationRepository.findTranslationByLanguageCode(languageCode)
                .getOrElse {
                    throw NotFoundException("Translation not found in language '$languageCode'")
                }

            val modifiedTranslation = translation.inferLanguageName()

            modifiedTranslation to translationDtoConverter.convertToDtoIf(modifiedTranslation) {
                requireDto
            }
        }

    override suspend fun resolveTranslation(
        languageCode: LanguageCode,
        requireDto: Boolean
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation> =
        runCatching {
            logger.d {
                "Resolving translation for language '$languageCode'..."
            }

            suspend fun convert(translation: TranslationEntity): EntityWithOptionalDto<TranslationEntity, Translation> =
                translation to translationDtoConverter.convertToDto(translation)

            val existingTranslation = getTranslation(languageCode).getOrNull()?.entity

            logger.v {
                "Existing translation: $existingTranslation"
            }

            existingTranslation?.let {
                return@runCatching convert(it)
            }

            val defaultTranslation = getDefaultTranslation().getOrNull()

            logger.v {
                "Default translation: $defaultTranslation"
            }

            defaultTranslation?.let {
                return@runCatching convert(it)
            }

            val firstAvailableTranslation = getTranslations().getOrNull()?.entities?.firstOrNull()

            logger.v {
                "First available translation: $firstAvailableTranslation"
            }

            firstAvailableTranslation?.let {
                return@runCatching convert(it)
            }

            throw UnprocessableEntityException("Couldn't resolve translation")
        }

    override suspend fun getTranslations(requireDto: Boolean): ResultingEntityWithOptionalDtoList<TranslationEntity, Translation> =
        runCatching {
            logger.d {
                "Obtaining translations..."
            }

            val translations = translationRepository.findTranslations().getOrThrow()

            val modifiedTranslations = translations.inferLanguageNames()

            modifiedTranslations to translationDtoConverter.convertToDtoListIf(modifiedTranslations) {
                requireDto
            }
        }

    override suspend fun updateTranslation(
        languageCode: LanguageCode,
        modification: TranslationEntity
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation> =
        translationRepository.withTransactionCatching {
            logger.d {
                "Updating translation in language '$languageCode'..."
            }

            val (originalTranslation, originalTranslationDto) = getTranslation(
                languageCode,
                requireDto = true
            ).getOrThrow()
            requireNotNull(originalTranslationDto)

            verifyLanguageName(modification)
            verifyDefaultTranslation(modification, originalTranslation.id)

            originalTranslation.apply {
                this.languageName = modification.languageName
                this.default = modification.default
                this.translations = modification.translations
            }

            val updatedTranslation = translationRepository.updateTranslation(originalTranslation)
                .getOrThrow()
                .inferLanguageName()

            val updatedTranslationDto = translationDtoConverter.convertToDto(updatedTranslation)

            sendTranslationUpdatedEvent(originalTranslationDto, updatedTranslationDto)

            updatedTranslation to updatedTranslationDto
        }

    override suspend fun removeTranslation(
        languageCode: LanguageCode
    ): Result<Unit> =
        translationRepository.withTransactionCatching {
            logger.d {
                "Removing translation in language '$languageCode'..."
            }

            val (translation, translationDto) = getTranslation(
                languageCode,
                requireDto = true
            ).getOrThrow()
            requireNotNull(translationDto)

            translationRepository.removeTranslation(translation).getOrThrow()

            sendTranslationRemovedEvent(translationDto)
        }

    override suspend fun translateBlock(block: BlockEntity, languageCode: LanguageCode): Result<Unit> =
        runCatching {
            logger.d {
                "Translating entities for block '${block.id}' in language '$languageCode'..."
            }

            val (translation, _) = resolveTranslation(languageCode).getOrThrow()

            val modifiedEntities = block.translateEntities(translation)
            block.entities = modifiedEntities
        }

    override suspend fun translateSpace(space: SpaceEntity, languageCode: LanguageCode): Result<Unit> =
        runCatching {
            logger.d {
                "Translating information of space '${space.id}' in language '$languageCode'..."
            }

            val (translation, _) = resolveTranslation(languageCode).getOrThrow()

            val modifiedView = space.translateView(translation)
            space.view = modifiedView
        }

    private suspend fun getDefaultTranslation(): Result<TranslationEntity> =
        translationRepository.findDefaultTranslation()

    private fun BlockEntity.translateEntities(translation: TranslationEntity): List<ContentEntity> {
        val modifiedEntities = mutableListOf<ContentEntity>()

        entities.forEach { entity ->
            val modifiedParameters = entity.translateParameters(translation)
            modifiedEntities += ContentEntity(entity.type, modifiedParameters, entity.metadata)
        }

        return modifiedEntities
    }

    private fun ContentEntity.translateParameters(translation: TranslationEntity): ContentEntityParameters {
        val modifiedParameters = mutableMapOf<ContentEntityParameterName, ContentEntityParameterValue>()

        parameters.forEach { parameter ->
            val isTranslatable = parameter.isTranslatable(contentEntity = this)

            if (isTranslatable) {
                val translatedValue = parameter.value.translateValue(translation)

                modifiedParameters[parameter.key] = translatedValue
            } else {
                modifiedParameters[parameter.key] = parameter.value
            }
        }

        return modifiedParameters
    }

    private fun SpaceEntity.translateView(translation: TranslationEntity) =
        with(view) {
            SpaceView(
                theme = theme,
                icon = icon?.translateValue(translation),
                favicon = favicon?.let { fav ->
                    SpaceFavicon(
                        appleTouch = fav.appleTouch?.translateValue(translation),
                        favicon16 = fav.favicon16?.translateValue(translation),
                        favicon32 = fav.favicon32?.translateValue(translation),
                        faviconIco = fav.faviconIco?.translateValue(translation)
                    )
                },
                preview = preview?.let { prev ->
                    SpaceUrlPreview(
                        type = prev.type,
                        image = prev.image?.translateValue(translation)
                    )
                },
                title = title?.translateValue(translation),
                description = description?.translateValue(translation),
                remark = remark?.translateValue(translation)
            )
        }

    private fun String.translateValue(translation: TranslationEntity) =
        foldTemplates { initial, template ->
            val location = template.extractTextLocation()
            val translatedText = translation.translations.getWithTextVarProcessing(location)

            initial.replace(template, translatedText ?: template)
        }

    private suspend fun verifyTranslationDoesntExist(
        translation: TranslationEntity
    ) {
        getTranslation(translation.languageCode).onSuccess {
            throw HttpStatusException(
                HttpStatusCode.Conflict,
                "Translation for language '${translation.languageCode}' already exists"
            )
        }
    }

    private fun verifyLanguageName(translation: TranslationEntity) {
        try {
            translation.inferLanguageName()
        } catch (_: IllegalStateException) {
            throw UnprocessableEntityException("Unable to infer language name of language code '${translation.languageCode}'")
        }
    }

    private suspend fun verifyDefaultTranslation(
        translation: TranslationEntity,
        translationId: UUID? = null
    ) {
        if (translation.default) {
            getDefaultTranslation().onSuccess {
                if (it.id != translationId) {
                    throw UnprocessableEntityException("Default translation already exists")
                }
            }
        }
    }

    private fun ContentEntityParameter.isTranslatable(contentEntity: ContentEntity): Boolean {
        logger.v {
            "Checking translatability of parameter $this in entity '${contentEntity.type}'"
        }

        val definition = definition(contentEntity.type)
        requireNotNull(definition)

        val definedParameters = definition.parameters

        logger.v {
            "Got defined parameters: $definedParameters"
        }

        val translatableParameterNames =
            definedParameters.filter {
                it.translatable
            }.map {
                it.name
            }

        logger.v {
            "Translatable parameters: $translatableParameterNames"
        }

        return this.key in translatableParameterNames
    }

    private fun String.foldTemplates(operation: (String, String) -> String): String {
        val extractedTemplates = Regex.Template.findAll(this).map { it.value }.toList()
        return extractedTemplates.fold(this, operation)
    }

    private fun TranslationEntity.inferLanguageName() =
        apply {
            if (languageName == null) {
                languageName = CommonLanguageCodes.byCode(languageCode)?.name
                    ?: error("Couldn't infer language code by language name '$languageName'")
            }
        }

    private fun TranslationEntities.inferLanguageNames(): TranslationEntities =
        map {
            it.inferLanguageName()
        }

    private fun String.extractTextLocation(): String {
        val location = Regex.TextLocation.find(this)?.value
        return requireNotNull(location) {
            "Unable to extract location from template: $this"
        }
    }

    private suspend fun sendTranslationCreatedEvent(translationDto: Translation) {
        val event = event(EventReferences.translationCreated) {
            translationDto
        }
        eventChannel.send(event)
    }

    private suspend fun sendTranslationUpdatedEvent(oldTranslationDto: Translation, newTranslationDto: Translation) {
        val eventRef = EventReferences.translationUpdated(newTranslationDto.languageCode)
        val update = EntityUpdate(oldTranslationDto, newTranslationDto)
        val event = event(eventRef) {
            update
        }
        eventChannel.send(event)
    }

    private suspend fun sendTranslationRemovedEvent(translationDto: Translation) {
        val eventRef = EventReferences.translationRemoved(translationDto.languageCode)
        val event = event(eventRef) {
            translationDto
        }
        eventChannel.send(event)
    }
}