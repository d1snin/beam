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

import dev.d1s.beam.commons.LanguageCode
import dev.d1s.beam.commons.SpaceIdentifier
import dev.d1s.beam.commons.Translation
import dev.d1s.beam.daemon.entity.BlockEntity
import dev.d1s.beam.daemon.entity.SpaceEntity
import dev.d1s.beam.daemon.entity.TranslationEntity
import dev.d1s.exkt.dto.ResultingEntityWithOptionalDto
import dev.d1s.exkt.dto.ResultingEntityWithOptionalDtoList
import org.koin.core.component.KoinComponent

interface TranslationService {

    suspend fun createTranslation(
        spaceIdentifier: SpaceIdentifier,
        translation: TranslationEntity
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation>

    suspend fun getTranslation(
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

    suspend fun removeTranslation(spaceIdentifier: SpaceIdentifier, languageCode: LanguageCode): Result<Unit>

    suspend fun translateEntities(block: BlockEntity, languageCode: LanguageCode): Result<Unit>

    companion object {

        const val GLOBAL_SPACE_IDENTIFIER = SpaceEntity.ROOT_SPACE_SLUG
    }
}

class DefaultTranslationService : TranslationService, KoinComponent {

    override suspend fun createTranslation(
        spaceIdentifier: SpaceIdentifier,
        translation: TranslationEntity
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation> {
        TODO("Not yet implemented")
    }

    override suspend fun getTranslation(
        spaceIdentifier: SpaceIdentifier?,
        languageCode: LanguageCode,
        requireDto: Boolean
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation> {
        TODO("Not yet implemented")
    }

    override suspend fun getTranslations(
        spaceIdentifier: SpaceIdentifier?,
        requireDto: Boolean
    ): ResultingEntityWithOptionalDtoList<TranslationEntity, Translation> {
        TODO("Not yet implemented")
    }

    override suspend fun updateTranslation(
        spaceIdentifier: SpaceIdentifier?,
        languageCode: LanguageCode,
        modification: TranslationEntity
    ): ResultingEntityWithOptionalDto<TranslationEntity, Translation> {
        TODO("Not yet implemented")
    }

    override suspend fun removeTranslation(spaceIdentifier: SpaceIdentifier, languageCode: LanguageCode): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun translateEntities(block: BlockEntity, languageCode: LanguageCode): Result<Unit> {
        TODO("Not yet implemented")
    }
}