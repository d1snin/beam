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

package dev.d1s.beam.bundle.configuration

import dev.d1s.beam.client.PublicBeamClient
import dev.d1s.beam.commons.DaemonState
import dev.d1s.exkt.ktor.server.koin.configuration.ApplicationConfigurer
import io.ktor.server.application.*
import io.ktor.server.config.*
import kotlinx.coroutines.*
import org.koin.core.module.Module
import org.lighthousegames.logging.logging
import kotlin.time.Duration.Companion.seconds

object BeamClient : ApplicationConfigurer {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val logger = logging()

    override fun Application.configure(module: Module, config: ApplicationConfig) {
        val httpAddress = config.daemonHttpAddress
        val wsAddress = config.daemonWsAddress

        val client = PublicBeamClient(httpAddress, wsAddress)

        runBlocking {
            val duration = 5.seconds

            logger.i {
                "Will request daemon status in $duration"
            }

            delay(duration)
        }

        ioScope.launch {
            val status = client.getDaemonStatus()

            status.onSuccess { (version, state) ->
                if (state == DaemonState.UP) {
                    logger.i {
                        "Connected to daemon. Version: $version. State: $state"
                    }
                } else {
                    throw IllegalStateException("Daemon isn't up. State: $state")
                }
            }

            status.onFailure {
                throw IllegalStateException("Couldn't request daemon status", it)
            }
        }

        module.single {
            client
        }
    }
}