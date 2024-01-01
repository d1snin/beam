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

package dev.d1s.beam.daemon.database

import dev.d1s.beam.commons.TranslationMap
import dev.d1s.beam.daemon.entity.TranslationEntity
import dev.d1s.exkt.ktorm.UuidIdentifiedEntities
import org.ktorm.jackson.json
import org.ktorm.schema.boolean
import org.ktorm.schema.text
import org.ktorm.schema.uuid

@Suppress("unused")
object Translations : UuidIdentifiedEntities<TranslationEntity>(tableName = "translation") {

    val spaceId = uuid("space_id").references(Spaces) {
        it.space
    }

    val languageCode = text("language_code").bindTo {
        it.languageCode
    }

    val languageName = text("language_name").bindTo {
        it.languageName
    }

    val default = boolean("default_flag").bindTo {
        it.default
    }

    val translations = json<TranslationMap>("translations").bindTo {
        it.translations
    }
}