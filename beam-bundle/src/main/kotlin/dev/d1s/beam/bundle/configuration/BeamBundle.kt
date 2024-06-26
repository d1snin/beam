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

import dev.d1s.exkt.ktor.server.koin.configuration.ApplicationConfigurer
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.core.module.Module
import org.lighthousegames.logging.logging

object BeamBundle : ApplicationConfigurer {

    private val logger = logging()

    override fun Application.configure(module: Module, config: ApplicationConfig) {
        logger.i {
            "Installing Beam Bundle..."
        }

        with(CheckStatic) {
            configure(module, config)
        }

        with(Routing) {
            configure(module, config)
        }

        with(StaticResources) {
            configure(module, config)
        }

        with(ApplicationConfigBean) {
            configure(module, config)
        }

        with(Services) {
            configure(module, config)
        }

        with(IndexModules) {
            configure(module, config)
        }

        with(HtmlRenderer) {
            configure(module, config)
        }
    }
}