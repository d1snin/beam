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

package dev.d1s.beam.bundle.configuration

import dev.d1s.beam.bundle.entity.SpaceRequest
import dev.d1s.beam.bundle.service.IndexService
import dev.d1s.beam.bundle.util.respondHtml
import dev.d1s.exkt.ktor.server.koin.configuration.ApplicationConfigurer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.lighthousegames.logging.logging

object StatusPages : ApplicationConfigurer, KoinComponent {

    private val indexService by inject<IndexService>()

    private val logger = logging()

    override fun Application.configure(module: Module, config: ApplicationConfig) {
        install(StatusPages) {
            status(HttpStatusCode.NotFound) { call, status ->
                logger.d {
                    "Handling $status. Path: ${call.request.path()}"
                }

                call.handleNotFound()
            }
        }
    }

    private suspend fun ApplicationCall.handleNotFound() {
        val request = SpaceRequest(spaceIdentifier = null, call = this)

        val resolvedSpace = indexService.resolveSpace(request)

        logger.d {
            "Responding not found..."
        }

        respondHtml(resolvedSpace.html)
    }
}