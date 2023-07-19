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

package dev.d1s.beam.bundle

import com.typesafe.config.ConfigFactory
import dev.d1s.beam.bundle.configuration.ApplicationConfigBean
import dev.d1s.beam.bundle.configuration.Routing
import dev.d1s.beam.daemon.BeamDaemonApplication
import dev.d1s.exkt.ktor.server.koin.configuration.Configurers
import dev.d1s.exkt.ktor.server.koin.configuration.ServerApplication
import dev.d1s.exkt.ktor.server.koin.configuration.builtin.Connector
import dev.d1s.exkt.ktor.server.koin.configuration.builtin.Di
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.core.component.KoinComponent
import org.lighthousegames.logging.logging

class BeamBundleApplication : ServerApplication(), KoinComponent {

    override val configurers: Configurers = listOf(
        Connector,
        Routing,
        ApplicationConfigBean,
        Di
    )

    private val logger = logging()

    override fun launch() {
        logger.i {
            "Starting Beam Bundle..."
        }

        val loadedConfig = loadConfig()
        val applicationEngineEnvironment = createApplicationEngineEnvironment(config = loadedConfig)

        applicationEngineEnvironment.monitor.subscribe(ApplicationStarted) {
            logger.i {
                "Beam Bundle is ready to accept requests on port ${applicationEngineEnvironment.config.port}"
            }
        }

        embeddedServer(Netty, applicationEngineEnvironment).start()

        BeamDaemonApplication().launch()
    }

    private fun loadConfig(): ApplicationConfig {
        val config = ConfigFactory.load("bundle")

        return HoconApplicationConfig(config)
    }
}