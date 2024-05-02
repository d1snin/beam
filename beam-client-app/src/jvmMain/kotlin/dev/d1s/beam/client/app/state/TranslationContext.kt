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

package dev.d1s.beam.client.app.state

import dev.d1s.beam.client.TranslationModificationBuilder
import dev.d1s.beam.client.app.ApplicationContext
import dev.d1s.beam.commons.LanguageCode
import io.ktor.client.plugins.*
import io.ktor.http.*
import org.lighthousegames.logging.logging

private val log = logging()

public suspend fun ApplicationContext.translation(
    languageCode: LanguageCode,
    configure: suspend TranslationModificationBuilder.() -> Unit
) {
    val modification = TranslationModificationBuilder().apply { configure() }.buildTranslationModification()

    log.i {
        "Creating translation in language '$languageCode'..."
    }

    client.postTranslation(languageCode, modification).getOrElse {
        val isConflict = it is ClientRequestException && it.response.status == HttpStatusCode.Conflict

        if (isConflict) {
            client.putTranslation(languageCode, modification).getOrThrow()
        } else {
            throw it
        }
    }
}