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

package dev.d1s.beam.commons.validation

import dev.d1s.beam.commons.SpaceIconUrl
import dev.d1s.beam.commons.SpaceThemeDefinition
import dev.d1s.beam.commons.SpaceThemeName
import dev.d1s.beam.commons.ViewConfiguration
import io.konform.validation.Validation
import io.konform.validation.ValidationBuilder
import io.ktor.http.*

internal val validateViewConfiguration: Validation<ViewConfiguration> = Validation {
    ViewConfiguration::theme {
        themeExists()
    }

    ViewConfiguration::icon ifPresent {
        correctUrl()
    }
}

private fun ValidationBuilder<SpaceThemeName>.themeExists() =
    addConstraint("space theme not found") { themeName ->
        SpaceThemeDefinition.byName(themeName) != null
    }

private fun ValidationBuilder<SpaceIconUrl>.correctUrl() =
    addConstraint("space icon URL is invalid") { url ->
        try {
            Url(url)
            true
        } catch (_: URLParserException) {
            false
        }
    }