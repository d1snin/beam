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

package dev.d1s.beam.daemon.route

import dev.d1s.beam.commons.Paths
import dev.d1s.beam.commons.TranslationModification
import dev.d1s.beam.commons.validation.validateTranslation
import dev.d1s.beam.daemon.configuration.DtoConverters
import dev.d1s.beam.daemon.entity.TranslationEntity
import dev.d1s.beam.daemon.service.TranslationService
import dev.d1s.beam.daemon.util.requiredLanguageCodeParameter
import dev.d1s.exkt.dto.DtoConverter
import dev.d1s.exkt.dto.requiredDto
import dev.d1s.exkt.ktor.server.koin.configuration.Route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class PostTranslationRoute : Route, KoinComponent {

    override val qualifier = named("post-translation-route")

    private val translationService by inject<TranslationService>()

    private val translationModificationDtoConverter by inject<DtoConverter<TranslationEntity, TranslationModification>>(
        DtoConverters.TranslationModificationDtoConverterQualifier
    )

    override fun Routing.apply() {
        authenticate {
            post(Paths.POST_TRANSLATION) {
                val languageCode = call.requiredLanguageCodeParameter

                val body = call.receive<TranslationModification>()
                validateTranslation(body)

                val translation = translationModificationDtoConverter.convertToEntity(body)

                val createdTranslation = translationService.createTranslation(languageCode, translation).getOrThrow()

                call.respond(HttpStatusCode.Created, createdTranslation.requiredDto)
            }
        }
    }
}