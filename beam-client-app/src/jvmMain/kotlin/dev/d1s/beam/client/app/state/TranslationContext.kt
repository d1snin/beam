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

package dev.d1s.beam.client.app.state

import dev.d1s.beam.client.TranslationModificationBuilder
import io.ktor.client.plugins.*
import io.ktor.http.*
import org.lighthousegames.logging.logging

private val log = logging()

public suspend fun SpaceContext.translation(configure: suspend TranslationModificationBuilder.() -> Unit) {
    val space = space.id

    val modification = TranslationModificationBuilder().apply { configure() }.buildTranslationModification()

    log.i {
        "Creating translation for space '$space' in language '${modification.languageCode}'..."
    }

    client.postTranslation(space, modification).getOrElse {
        val isConflict = it is ClientRequestException && it.response.status == HttpStatusCode.Conflict

        if (isConflict) {
            client.putTranslation(space, modification.languageCode, modification).getOrThrow()
        } else {
            throw it
        }
    }
}