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
import dev.d1s.ktor.events.commons.event
import dev.d1s.ktor.events.server.WebSocketEventChannel
import io.ktor.http.*
import io.ktor.server.plugins.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import java.util.*

interface TranslationService {

    suspend fun createTranslation(
        spaceIdentifier: SpaceIdentifier?,
        translation: TranslationEntity
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation>

    suspend fun getTranslation(
        spaceIdentifier: SpaceIdentifier?,
        languageCode: LanguageCode,
        requireDto: Boolean = false
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation>

    suspend fun resolveTranslation(
        spaceIdentifier: SpaceIdentifier?,
        languageCode: LanguageCode,
        requireDto: Boolean = false
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation>

    suspend fun getTranslations(
        spaceIdentifier: SpaceIdentifier?,
        requireDto: Boolean = false
    ): ResultingEntityWithOptionalDtoList<TranslationEntity, Translation>

    suspend fun updateTranslation(
        spaceIdentifier: SpaceIdentifier?,
        languageCode: LanguageCode,
        modification: TranslationEntity
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation>

    suspend fun removeTranslation(spaceIdentifier: SpaceIdentifier?, languageCode: LanguageCode): Result<Unit>

    suspend fun translateBlock(block: BlockEntity, languageCode: LanguageCode): Result<Unit>

    suspend fun translateSpace(space: SpaceEntity, languageCode: LanguageCode): Result<Unit>

    suspend fun verifyLocationsExist(block: BlockEntity): Result<Unit>

    suspend fun verifyLocationsExist(space: SpaceEntity, explicitId: SpaceId? = null): Result<Unit>

    suspend fun verifyLocationsNotUsed(space: SpaceEntity): Result<Unit>

    companion object {

        const val GLOBAL_TRANSLATION_PERMITTED_SPACE = SpaceEntity.ROOT_SPACE_SLUG
    }
}

class DefaultTranslationService : TranslationService, KoinComponent {

    private val translationRepository by inject<TranslationRepository>()

    private val translationDtoConverter by inject<DtoConverter<TranslationEntity, Translation>>(DtoConverters.TranslationDtoConverterQualifier)

    private val eventChannel by inject<WebSocketEventChannel>()

    private val blockService by inject<BlockService>()

    private val spaceService by inject<SpaceService>()

    private val logger = logging()

    override suspend fun createTranslation(
        spaceIdentifier: SpaceIdentifier?,
        translation: TranslationEntity
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation> =
        runCatching {
            logger.d {
                "Creating translation ${translation.asString}..."
            }

            verifyTranslationDoesntExist(spaceIdentifier, translation)
            verifyLocationsSatisfied(spaceIdentifier, translation)
            verifyLanguageName(translation)
            verifyDefaultTranslation(translation, spaceIdentifier)

            spaceIdentifier?.let {
                val (space, _) = spaceService.getSpace(it).getOrThrow()
                translation.space = space
            }

            val addedTranslation = translationRepository.addTranslation(translation)
                .getOrThrow()
                .inferLanguageName()

            val addedTranslationDto = translationDtoConverter.convertToDto(addedTranslation)
            sendTranslationCreatedEvent(addedTranslationDto)

            addedTranslation to addedTranslationDto
        }

    override suspend fun getTranslation(
        spaceIdentifier: SpaceIdentifier?,
        languageCode: LanguageCode,
        requireDto: Boolean
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation> =
        runCatching {
            logger.d {
                "Obtaining translation for space '$spaceIdentifier' in language '$languageCode'..."
            }

            val space = spaceIdentifier?.let {
                spaceService.getSpace(it).getOrThrow()
            }?.entity

            val translation = translationRepository.findTranslationBySpaceAndLanguageCode(space, languageCode)
                .getOrElse {
                    val message = spaceIdentifier?.let {
                        "Translation not found by space '${space?.id}' and language code '$languageCode'"
                    } ?: "Global translation not found for language '$languageCode'"

                    throw NotFoundException(message)
                }

            val modifiedTranslation = translation.inferLanguageName()

            modifiedTranslation to translationDtoConverter.convertToDtoIf(modifiedTranslation) {
                requireDto
            }
        }

    override suspend fun resolveTranslation(
        spaceIdentifier: SpaceIdentifier?,
        languageCode: LanguageCode,
        requireDto: Boolean
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation> =
        runCatching {
            logger.d {
                "Resolving translation for space '$spaceIdentifier'..."
            }

            suspend fun convert(translation: TranslationEntity): EntityWithOptionalDto<TranslationEntity, Translation> =
                translation to translationDtoConverter.convertToDto(translation)

            val existingTranslation = getTranslation(spaceIdentifier, languageCode).getOrNull()?.entity

            existingTranslation?.let {
                return@runCatching convert(it)
            }

            val defaultTranslation = getDefaultTranslation(spaceIdentifier).getOrNull()

            defaultTranslation?.let {
                return@runCatching convert(it)
            }

            val firstAvailableTranslation = getTranslations(spaceIdentifier).getOrNull()?.entities?.firstOrNull()

            firstAvailableTranslation?.let {
                return@runCatching convert(it)
            }

            throw UnprocessableEntityException("Couldn't resolve translation")
        }

    // Боже, да что с ней происходит?
    // Она же... кто она вообще?
    // Разве это та, кого я полюбил?
    // Почему то, что было в прошлом так сильно отзывается в настоящем?
    // Ты не можешь отпустить это, ты будто живешь только этим.
    // Да, мы - результат сложения событий прошлого.
    // Но...

    override suspend fun getTranslations(
        spaceIdentifier: SpaceIdentifier?,
        requireDto: Boolean
    ): ResultingEntityWithOptionalDtoList<TranslationEntity, Translation> =
        runCatching {
            logger.d {
                "Obtaining translations for space '$spaceIdentifier'..."
            }

            val space = spaceIdentifier?.let {
                spaceService.getSpace(spaceIdentifier).getOrThrow()
            }?.entity

            val translations = translationRepository.findTranslationsBySpace(space).getOrThrow()

            val modifiedTranslations = translations.inferLanguageNames()

            modifiedTranslations to translationDtoConverter.convertToDtoListIf(modifiedTranslations) {
                requireDto
            }
        }

    override suspend fun updateTranslation(
        spaceIdentifier: SpaceIdentifier?,
        languageCode: LanguageCode,
        modification: TranslationEntity
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation> =
        runCatching {
            logger.d {
                "Updating translation for space '$spaceIdentifier' in language '$languageCode'..."
            }

            val (originalTranslation, originalTranslationDto) = getTranslation(
                spaceIdentifier,
                languageCode,
                requireDto = true
            ).getOrThrow()
            requireNotNull(originalTranslationDto)

            verifyLocationsSatisfied(spaceIdentifier, modification)
            verifyLanguageName(modification)
            verifyDefaultTranslation(modification, spaceIdentifier, originalTranslation.id)

            originalTranslation.apply {
                this.languageCode = modification.languageCode
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
        spaceIdentifier: SpaceIdentifier?,
        languageCode: LanguageCode
    ): Result<Unit> =
        runCatching {
            logger.d {
                "Removing translation for space '$spaceIdentifier' in language '$languageCode'..."
            }

            val (translation, translationDto) = getTranslation(
                spaceIdentifier,
                languageCode,
                requireDto = true
            ).getOrThrow()
            requireNotNull(translationDto)

            verifyLocationsSatisfied(spaceIdentifier, translation, removing = true)

            translationRepository.removeTranslation(translation).getOrThrow()

            sendTranslationRemovedEvent(translationDto)
        }

    override suspend fun translateBlock(block: BlockEntity, languageCode: LanguageCode): Result<Unit> =
        runCatching {
            logger.d {
                "Translating entities for block '${block.id}' in language '$languageCode'..."
            }

            val (translation, _) = getTranslation(block.space.id.toString(), languageCode).getOrThrow()

            val modifiedEntities = block.translateEntities(translation)
            block.entities = modifiedEntities
        }

    override suspend fun translateSpace(space: SpaceEntity, languageCode: LanguageCode): Result<Unit> =
        runCatching {
            logger.d {
                "Translating information of space '${space.id}' in language '$languageCode'..."
            }

            val (translation, _) = getTranslation(space.id.toString(), languageCode).getOrThrow()

            val modifiedView = space.translateView(translation)
            space.view = modifiedView
        }

    override suspend fun verifyLocationsExist(block: BlockEntity): Result<Unit> =
        runCatching {
            logger.v {
                "Verifying locations exist on block"
            }

            val usedLocations = block.extractLocations()

            logger.v {
                "Used locations: $usedLocations"
            }

            verifyUsedLocationsExist(usedLocations, block.space)
        }

    override suspend fun verifyLocationsExist(space: SpaceEntity, explicitId: SpaceId?): Result<Unit> =
        runCatching {
            logger.v {
                "Verifying locations exist on space view configuration"
            }

            val usedLocations = space.extractLocations()

            logger.v {
                "Used locations: $usedLocations"
            }

            verifyUsedLocationsExist(usedLocations, space, explicitId)
        }

    override suspend fun verifyLocationsNotUsed(space: SpaceEntity): Result<Unit> =
        runCatching {
            logger.v {
                "Verifying locations not used on space view configuration"
            }

            val usedLocations = space.extractLocations()

            logger.v {
                "Used locations: $usedLocations"
            }

            if (usedLocations.isNotEmpty()) {
                throw BadRequestException("Unable to process locations on newly creating space")
            }
        }

    private suspend fun getDefaultTranslation(spaceIdentifier: SpaceIdentifier?): Result<TranslationEntity> {
        val space = spaceIdentifier?.let {
            spaceService.getSpace(spaceIdentifier).getOrNull()
        }?.entity

        return translationRepository.findDefaultTranslation(space)
    }

    private suspend fun verifyUsedLocationsExist(
        usedLocations: List<TextLocation>,
        space: SpaceEntity,
        explicitId: SpaceId? = null
    ) {
        val availableLocations = getAvailableSpaceLocations(explicitId ?: space.id.toString())

        logger.v {
            "Available locations: $availableLocations"
        }

        val unavailableLocations = mutableListOf<TextLocation>()

        usedLocations.forEach { usedLocation ->
            if (usedLocation !in availableLocations) {
                unavailableLocations += usedLocation
            }
        }

        if (unavailableLocations.isNotEmpty()) {
            throw BadRequestException("The following locations are not available: $unavailableLocations")
        }
    }

    private fun BlockEntity.translateEntities(translation: TranslationEntity): List<ContentEntity> {
        val modifiedEntities = mutableListOf<ContentEntity>()

        entities.forEach { entity ->
            val modifiedParameters = entity.translateParameters(translation)
            modifiedEntities += ContentEntity(entity.type, modifiedParameters)
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
            ViewConfiguration(
                theme = theme,
                icon = icon?.translateValue(translation),
                favicon = favicon?.let { fav ->
                    SpaceFavicon(
                        appleTouch = fav.appleTouch?.translateValue(translation),
                        favicon16 = fav.favicon16?.translateValue(translation),
                        favicon32 = fav.favicon32?.translateValue(translation),
                        faviconIco = fav.faviconIco?.translateValue(translation),
                        browserconfig = fav.browserconfig?.translateValue(translation),
                        maskIcon = fav.maskIcon?.translateValue(translation),
                        maskIconColor = fav.maskIconColor
                    )
                },
                title = title?.translateValue(translation),
                description = description?.translateValue(translation),
            )
        }

    private fun String.translateValue(translation: TranslationEntity) =
        foldTemplates { initial, template ->
            val location = template.extractTextLocation()
            val translatedText = translation.translations[location]
                ?: error("Unable to find location '$location' in translation")

            initial.replace(template, translatedText)
        }

    private suspend fun verifyTranslationDoesntExist(
        spaceIdentifier: SpaceIdentifier?,
        translation: TranslationEntity
    ) {
        getTranslation(spaceIdentifier, translation.languageCode).onSuccess {
            val message = spaceIdentifier?.let {
                "Translation for language '${translation.languageCode}' associated with space '$it' already exists"
            } ?: "Global translation for language '${translation.languageCode}' already exists"

            throw HttpStatusException(HttpStatusCode.Conflict, message)
        }
    }

    private suspend fun verifyLocationsSatisfied(
        spaceIdentifier: SpaceIdentifier?,
        translation: TranslationEntity,
        removing: Boolean = false
    ) {
        if (removing) {
            verifyAtLeastOneTranslationExistsAfterRemoval(spaceIdentifier, translation)
        } else {
            val locations = getUsedSpaceLocations(spaceIdentifier)
            val unsatisfiedLocations = locations.toMutableList()

            locations.forEach { location ->
                if (location in translation.translations.keys) {
                    unsatisfiedLocations.remove(location)
                }
            }

            if (unsatisfiedLocations.isNotEmpty()) {
                throw BadRequestException("The following locations aren't satisfied: $locations")
            }
        }
    }

    private suspend fun verifyAtLeastOneTranslationExistsAfterRemoval(
        spaceIdentifier: SpaceIdentifier?,
        translation: TranslationEntity
    ) {
        val locations = getUsedSpaceLocations(spaceIdentifier, excludeGlobals = true)
        val (translations, _) = getTranslations(spaceIdentifier).getOrThrow()
        val filteredTranslations = translations - translation

        if (locations.isNotEmpty() && filteredTranslations.isEmpty()) {
            throw UnprocessableEntityException("Unable to remove translation since it's the last remaining one and is used by at least one location")
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
        spaceIdentifier: SpaceIdentifier?,
        translationId: UUID? = null
    ) {
        if (translation.default) {
            getDefaultTranslation(spaceIdentifier).onSuccess {
                if (it.id != translationId) {
                    throw UnprocessableEntityException("Default translation already exists")
                }
            }
        }
    }

    private suspend fun getAvailableSpaceLocations(spaceIdentifier: SpaceIdentifier): List<TextLocation> {
        val locations = mutableSetOf<TextLocation>()
        locations += GlobalTranslation.Locations

        val (translations, _) = getTranslations(spaceIdentifier).getOrThrow()

        translations.forEach { translation ->
            translation.translations.forEach { (location, _) ->
                locations += location
            }
        }

        return locations.toList()
    }

    private suspend fun getUsedSpaceLocations(
        spaceIdentifier: SpaceIdentifier?,
        excludeGlobals: Boolean = false
    ): List<TextLocation> {
        val locations = mutableSetOf<TextLocation>()
        locations += GlobalTranslation.Locations

        spaceIdentifier?.let {
            val (blocks, _) = blockService.getBlocks(it).getOrThrow()

            blocks.forEach { block ->
                locations += block.extractLocations()
            }
        }

        if (excludeGlobals) {
            locations -= GlobalTranslation.Locations.toSet()
        }

        return locations.toList()
    }

    private fun BlockEntity.extractLocations(): List<TextLocation> {
        logger.v {
            "Extracting used locations from block"
        }

        val locations = mutableSetOf<TextLocation>()

        entities.forEach { entity ->
            val parameters = entity.parameters.filterTranslatableContentEntityParameters(entity)

            logger.v {
                "Translatable parameters for entity ${entity.type}: $parameters"
            }

            parameters.forEach { (_, value) ->
                locations += value.extractLocations()
            }
        }

        logger.v {
            "Extracted locations: $locations"
        }

        return locations.toList()
    }

    private fun SpaceEntity.extractLocations(): List<TextLocation> {
        logger.v {
            "Extracting used locations from space"
        }

        val locations = mutableSetOf<TextLocation>()

        fun String?.extractAndAddLocations() {
            this?.let {
                locations += it.extractLocations()
            }
        }

        with(view) {
            icon.extractAndAddLocations()

            favicon?.run {
                appleTouch.extractAndAddLocations()
                favicon16.extractAndAddLocations()
                favicon32.extractAndAddLocations()
                faviconIco.extractAndAddLocations()
                browserconfig.extractAndAddLocations()
                maskIcon.extractAndAddLocations()
                maskIconColor.extractAndAddLocations()
            }

            title.extractAndAddLocations()
            description.extractAndAddLocations()
        }

        return locations.toList()
    }

    private fun String.extractLocations(): List<TextLocation> =
        buildList {
            foldTemplates { initial, template ->
                val location = template.extractTextLocation()

                add(location)

                initial
            }
        }

    private fun ContentEntityParameters.filterTranslatableContentEntityParameters(contentEntity: ContentEntity): ContentEntityParameters =
        filter { parameter ->
            parameter.isTranslatable(contentEntity)
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
        val event = event(EventReferences.translationCreated, translationDto)
        eventChannel.send(event)
    }

    private suspend fun sendTranslationUpdatedEvent(oldTranslationDto: Translation, newTranslationDto: Translation) {
        val qualifier = newTranslationDto.qualifier()
        val eventRef = EventReferences.translationUpdated(qualifier)
        val update = EntityUpdate(oldTranslationDto, newTranslationDto)
        val event = event(eventRef, update)
        eventChannel.send(event)
    }

    private suspend fun sendTranslationRemovedEvent(translationDto: Translation) {
        val qualifier = translationDto.qualifier()
        val eventRef = EventReferences.translationRemoved(qualifier)
        val event = event(eventRef, translationDto)
        eventChannel.send(event)
    }

    private fun Translation.qualifier() =
        TranslationQualifier(space, languageCode)
}