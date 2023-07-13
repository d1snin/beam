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

package dev.d1s.beam.daemon

import dev.d1s.beam.daemon.configuration.*
import dev.d1s.exkt.ktor.server.koin.configuration.Configurers
import dev.d1s.exkt.ktor.server.koin.configuration.ServerApplication
import dev.d1s.exkt.ktor.server.koin.configuration.builtin.Connector
import dev.d1s.exkt.ktor.server.koin.configuration.builtin.Di
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.core.component.KoinComponent
import org.lighthousegames.logging.logging

public class BeamDaemonApplication : ServerApplication(), KoinComponent {

    override val configurers: Configurers = listOf(
        Connector,
        ApplicationConfigBean,
        ContentNegotiation,
        Database,
        Services,
        Repositories,
        DtoConverters,
        Routing,
        WebSocketEvents,
        Cors,
        Security,
        RateLimit,
        StatusPages,
        Di
    )

    private val logger = logging()

    override fun launch() {
        logger.i {
            "Starting Beam Daemon..."
        }

        val applicationEngineEnvironment = createApplicationEngineEnvironment()

        applicationEngineEnvironment.monitor.subscribe(ApplicationStarted) {
            logger.i {
                "Beam Daemon is ready to accept requests on port ${applicationEngineEnvironment.config.port}"
            }
        }

        embeddedServer(Netty, applicationEngineEnvironment).start(wait = true)
    }
}