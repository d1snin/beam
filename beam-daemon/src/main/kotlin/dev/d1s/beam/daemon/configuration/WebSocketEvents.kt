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

package dev.d1s.beam.daemon.configuration

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import dev.d1s.exkt.ktor.server.koin.configuration.ApplicationConfigurer
import dev.d1s.ktor.events.server.WebSocketEventChannel
import dev.d1s.ktor.events.server.WebSocketEvents
import dev.d1s.ktor.events.server.webSocketEvents
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import org.koin.core.module.Module

object WebSocketEvents : ApplicationConfigurer {

    override fun Application.configure(module: Module, config: ApplicationConfig) {
        install(WebSockets) {
            val mapper = jacksonMapperBuilder().addModule(JavaTimeModule()).build()
            contentConverter = JacksonWebsocketContentConverter(mapper)
        }

        val eventChannel = WebSocketEventChannel()

        install(WebSocketEvents) {
            channel = eventChannel
        }

        routing {
            webSocketEvents()
        }

        module.single {
            eventChannel
        }
    }
}