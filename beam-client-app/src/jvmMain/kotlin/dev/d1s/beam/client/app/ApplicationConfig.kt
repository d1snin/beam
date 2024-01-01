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

package dev.d1s.beam.client.app

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.sources.EnvironmentVariablesPropertySource
import dev.d1s.beam.client.BeamDaemonBaseUrl
import dev.d1s.beam.commons.SpaceToken

private const val ENV_VAR_PREFIX = "BEAM_APP__"

public data class ApplicationConfig(
    val httpBaseUrl: BeamDaemonBaseUrl,
    val token: SpaceToken
)

internal fun defaultApplicationConfig(): ApplicationConfig {
    val propertySource = EnvironmentVariablesPropertySource(
        useUnderscoresAsSeparator = true,
        allowUppercaseNames = true,
        prefix = ENV_VAR_PREFIX
    )

    return ConfigLoaderBuilder.default()
        .addPropertySource(propertySource)
        .build()
        .loadConfigOrThrow()
}