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
import dev.d1s.beam.bundle.configuration.*
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

object BeamBundleApplication : ServerApplication(), KoinComponent {

    override val configurers: Configurers = listOf(
        CheckStatic,
        Connector,
        Routing,
        StaticResources,
        Cors,
        ApplicationConfigBean,
        BeamClient,
        Services,
        HtmlRenderer,
        Di
    )

    private val logger = logging()

    override fun launch() {
        logger.i {
            "Starting Beam Bundle..."
        }

        val config = loadConfig()
        val environment = createApplicationEngineEnvironment(config = config)
        startBundle(environment)
    }

    private fun startBundle(environment: ApplicationEngineEnvironment) {
        val config = environment.config

        environment.monitor.subscribe(ServerReady) {
            logger.i {
                "Beam Bundle is ready to accept requests on port ${config.port}"
            }
        }

        embeddedServer(Netty, environment).start(wait = true)
    }

    private fun loadConfig(): ApplicationConfig {
        val config = ConfigFactory.load("bundle")

        return HoconApplicationConfig(config)
    }
}