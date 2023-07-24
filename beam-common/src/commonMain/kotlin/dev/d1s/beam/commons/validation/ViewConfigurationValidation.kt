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

import dev.d1s.beam.commons.SpaceFavicon
import dev.d1s.beam.commons.SpaceThemeDefinition
import dev.d1s.beam.commons.SpaceThemeName
import dev.d1s.beam.commons.Url
import dev.d1s.beam.commons.ViewConfiguration
import dev.d1s.exkt.konform.isNotBlank
import io.konform.validation.Validation
import io.konform.validation.ValidationBuilder
import io.konform.validation.jsonschema.maxLength
import io.ktor.http.*

internal val validateViewConfiguration: Validation<ViewConfiguration> = Validation {
    ViewConfiguration::theme {
        themeExists()
    }

    ViewConfiguration::icon ifPresent {
        correctUrl()
    }

    ViewConfiguration::favicon ifPresent {
        validFavicon()
    }

    ViewConfiguration::title ifPresent {
        isNotBlank() hint "view title must not be blank"
        maxLength(Limits.VIEW_TITLE_MAX_LENGTH) hint "view title must be shorter than ${Limits.VIEW_TITLE_MAX_LENGTH} characters"
    }

    ViewConfiguration::description ifPresent {
        isNotBlank() hint "view description must not be blank"
        maxLength(Limits.VIEW_DESCRIPTION_MAX_LENGTH) hint "view description must be shorter than ${Limits.VIEW_DESCRIPTION_MAX_LENGTH} characters"
    }
}

private fun ValidationBuilder<SpaceFavicon>.validFavicon() {
    SpaceFavicon::appleTouch ifPresent {
        correctUrl()
    }

    SpaceFavicon::favicon16 ifPresent {
        correctUrl()
    }

    SpaceFavicon::favicon32 ifPresent {
        correctUrl()
    }

    SpaceFavicon::faviconIco ifPresent {
        correctUrl()
    }

    SpaceFavicon::browserconfig ifPresent {
        correctUrl()
    }

    SpaceFavicon::maskIcon ifPresent {
        correctUrl()
    }

    SpaceFavicon::maskIconColor ifPresent {
        isNotBlank() hint "mask icon color must not be blank"
    }
}

private fun ValidationBuilder<SpaceThemeName>.themeExists() =
    addConstraint("space theme not found") { themeName ->
        SpaceThemeDefinition.byName(themeName) != null
    }

private fun ValidationBuilder<Url>.correctUrl() =
    addConstraint("URL is invalid") { url ->
        try {
            Url(url)
            true
        } catch (_: URLParserException) {
            false
        }
    }