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
import dev.d1s.beam.daemon.service.TranslationService
import dev.d1s.beam.daemon.util.requiredLanguageCodeParameter
import dev.d1s.exkt.dto.requiredDto
import dev.d1s.exkt.ktor.server.koin.configuration.Route
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class GetResolvedTranslationRoute : Route, KoinComponent {

    override val qualifier = named("get-resolved-translation-route")

    private val translationService by inject<TranslationService>()

    override fun Routing.apply() {
        get(Paths.GET_RESOLVED_TRANSLATION) {
            val languageCode = call.requiredLanguageCodeParameter

            val resolvedTranslation =
                translationService.resolveTranslation(languageCode, requireDto = true).getOrThrow()

            call.respond(resolvedTranslation.requiredDto)
        }
    }
}