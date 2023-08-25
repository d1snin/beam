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

import dev.d1s.beam.commons.LanguageCode
import dev.d1s.beam.daemon.entity.SpaceEntity
import dev.d1s.beam.daemon.entity.TranslationEntities
import dev.d1s.beam.daemon.entity.TranslationEntity
import dev.d1s.beam.daemon.util.withIoCatching
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.toList

interface TranslationRepository {

    suspend fun addTranslation(translation: TranslationEntity): Result<TranslationEntity>

    suspend fun findTranslationsBySpace(space: SpaceEntity): Result<TranslationEntities>

    suspend fun findTranslationBySpaceAndLanguageCode(
        space: SpaceEntity,
        languageCode: LanguageCode
    ): Result<TranslationEntity>

    suspend fun updateTranslation(translation: TranslationEntity): Result<TranslationEntity>

    suspend fun removeTranslation(translation: TranslationEntity): Result<Unit>
}

class DefaultTranslationRepository : TranslationRepository, KoinComponent {

    private val database by inject<Database>()

    override suspend fun addTranslation(translation: TranslationEntity): Result<TranslationEntity> =
        withIoCatching {
            translation.apply {
                database.translations.add(this)
            }
        }

    override suspend fun findTranslationsBySpace(space: SpaceEntity): Result<TranslationEntities> =
        withIoCatching {
            database.translations.filter {
                it.spaceId eq space.id
            }.toList()
        }

    override suspend fun findTranslationBySpaceAndLanguageCode(
        space: SpaceEntity,
        languageCode: LanguageCode
    ): Result<TranslationEntity> =
        withIoCatching {
            database.translations.find {
                (it.spaceId eq space.id) and (it.languageCode eq languageCode)
            } ?: error("Translation not found by space ${space.id} and language code '$languageCode'")
        }

    override suspend fun updateTranslation(translation: TranslationEntity): Result<TranslationEntity> =
        withIoCatching {
            translation.apply {
                flushChanges()
            }
        }

    override suspend fun removeTranslation(translation: TranslationEntity): Result<Unit> =
        withIoCatching {
            translation.delete()
            Unit
        }
}