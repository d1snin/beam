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

object CheckStatic : ApplicationConfigurer {

    private val requiredResources = listOf(
        "main.bundle.js"
    )

    override fun Application.configure(module: Module, config: ApplicationConfig) {
        requiredResources.forEach { resource ->
            environment.checkResourceExists(resource)
        }
    }

    private fun ApplicationEnvironment.checkResourceExists(resource: String) {
        val path = "${StaticResources.STATIC_PACKAGE}/$resource"
        classLoader.getResource(path) ?: error("Couldn't locate required resource at $path")
    }
}