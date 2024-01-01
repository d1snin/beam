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

import dev.d1s.exkt.ktor.server.koin.configuration.ApplicationConfigurer
import dev.d1s.exkt.ktor.server.statuspages.HttpStatusException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import org.koin.core.module.Module
import io.ktor.server.plugins.statuspages.StatusPages as StatusPagesPlugin

object StatusPages : ApplicationConfigurer {

    override fun Application.configure(module: Module, config: ApplicationConfig) {
        install(StatusPagesPlugin) {
            handleExceptions()
            handleStatuses()
        }
    }

    private fun StatusPagesConfig.handleExceptions() {
        exception<Throwable> { call, throwable ->
            val status = when (throwable) {
                is BadRequestException -> HttpStatusCode.BadRequest
                is NotFoundException -> HttpStatusCode.NotFound
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
        status(HttpStatusCode.BadRequest, HttpStatusCode.NotFound, HttpStatusCode.Unauthorized) { call, status ->
            val message = Message(status.description)
            call.respond(message)
        }
    }

    private fun Throwable.toMessage(statusCode: HttpStatusCode) =
        Message((message ?: "No message").takeIf { statusCode != HttpStatusCode.InternalServerError }
            ?: HttpStatusCode.InternalServerError.description)

    @Serializable
    private data class Message(
        val message: String
    )
}