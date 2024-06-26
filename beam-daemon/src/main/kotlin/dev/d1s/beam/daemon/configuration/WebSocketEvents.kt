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

import dev.d1s.beam.commons.Paths
import dev.d1s.exkt.ktor.server.koin.configuration.ApplicationConfigurer
import dev.d1s.ktor.events.server.WebSocketEventChannel
import dev.d1s.ktor.events.server.WebSocketEvents
import dev.d1s.ktor.events.server.webSocketEvents
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.routing.*
import org.koin.core.module.Module

object WebSocketEvents : ApplicationConfigurer {

    override fun Application.configure(module: Module, config: ApplicationConfig) {
        val eventChannel = WebSocketEventChannel()

        install(WebSocketEvents) {
            channel = eventChannel
        }

        routing {
            webSocketEvents(route = Paths.EVENTS)
        }

        module.single {
            eventChannel
        }
    }
}