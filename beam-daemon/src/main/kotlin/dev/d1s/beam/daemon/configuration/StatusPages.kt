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

package dev.d1s.beam.daemon.configuration

import dev.d1s.beam.bundle.entity.SpaceRequest
import dev.d1s.beam.bundle.service.IndexService
import dev.d1s.beam.bundle.util.respondHtml
import dev.d1s.beam.commons.Paths
import dev.d1s.exkt.ktor.server.koin.configuration.ApplicationConfigurer
import dev.d1s.exkt.ktor.server.statuspages.HttpStatusException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.lighthousegames.logging.logging
import io.ktor.server.plugins.statuspages.StatusPages as StatusPagesPlugin

object StatusPages : ApplicationConfigurer, KoinComponent {

    private val indexService by inject<IndexService>()

    private val logger = logging()

    override fun Application.configure(module: Module, config: ApplicationConfig) {
        install(StatusPagesPlugin) {
            handleExceptions()
            handleStatuses()
            handleNotFoundStatus()
        }
    }

    private fun StatusPagesConfig.handleExceptions() {
        exception<Throwable> { call, throwable ->
            logger.d {
                "Handling exception ${throwable::class.simpleName}: ${throwable.message}"
            }

            val status = when (throwable) {
                is NotFoundException -> {
                    call.processNotFoundStatus()
                    return@exception
                }

                is BadRequestException -> HttpStatusCode.BadRequest
                is HttpStatusException -> throwable.status
                else -> HttpStatusCode.InternalServerError.also {
                    throwable.printStackTrace()
                }
            }

            val message = throwable.toMessage(status)
            call.respond(status, message)
        }
    }

    private fun StatusPagesConfig.handleStatuses() {
        status(HttpStatusCode.BadRequest, HttpStatusCode.Unauthorized) { call, status ->
            logger.d {
                "Handling status $status on path ${call.request.path()}"
            }

            val message = Message(status.description)
            call.respond(message)
        }
    }

    private fun StatusPagesConfig.handleNotFoundStatus() {
        status(HttpStatusCode.NotFound) { call, _ ->
            logger.d {
                "Handling 404 code on path ${call.request.path()}"
            }

            call.processNotFoundStatus()
        }
    }

    private suspend fun ApplicationCall.processNotFoundStatus() {
        routeResponse(
            onApi = {
                val message = Message(HttpStatusCode.NotFound.description)
                respond(message)
            },
            onBundle = {
                handleHtmlNotFound()
            }
        )
    }

    private suspend fun ApplicationCall.routeResponse(
        onApi: suspend ApplicationCall.() -> Unit,
        onBundle: suspend ApplicationCall.() -> Unit
    ) {
        if (request.path().startsWith(Paths.DAEMON_BASE)) {
            onApi()
        } else {
            onBundle()
        }
    }

    private suspend fun ApplicationCall.handleHtmlNotFound() {
        val request = SpaceRequest(spaceIdentifier = null, this)
        val resolvedSpace = indexService.resolveSpace(request)

        respondHtml(resolvedSpace.html)
    }

    private fun Throwable.toMessage(statusCode: HttpStatusCode) =
        Message((message ?: "No message").takeIf { statusCode != HttpStatusCode.InternalServerError }
            ?: HttpStatusCode.InternalServerError.description)

    @Serializable
    private data class Message(
        val message: String
    )
}