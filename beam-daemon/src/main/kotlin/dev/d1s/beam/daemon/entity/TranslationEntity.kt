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

package dev.d1s.beam.daemon.entity

import dev.d1s.beam.commons.LanguageCode
import dev.d1s.beam.commons.LanguageName
import dev.d1s.beam.commons.TranslationMap
import org.ktorm.entity.Entity

typealias TranslationEntities = List<TranslationEntity>

interface TranslationEntity : Entity<TranslationEntity> {

    var space: SpaceEntity?

    var languageCode: LanguageCode

    var languageName: LanguageName?

    var translations: TranslationMap

    companion object : Entity.Factory<TranslationEntity>()
}

val TranslationEntity.asString
    get() = "TranslationEntity{space = $space, languageCode = $languageCode, languageName = $languageName}"

val TranslationEntity.isGlobal get() = space == null