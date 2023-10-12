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

import dev.d1s.beam.commons.*
import dev.d1s.exkt.konform.isNotBlank
import io.konform.validation.Validation
import io.konform.validation.ValidationBuilder
import io.konform.validation.jsonschema.maxLength

internal val validateSpaceView: Validation<SpaceView> = Validation {
    SpaceView::theme {
        themeExists()
    }

    SpaceView::icon ifPresent {
        correctUrl()
    }

    SpaceView::favicon ifPresent {
        validFavicon()
    }

    SpaceView::preview ifPresent {
        validatePreview()
    }

    SpaceView::title ifPresent {
        isNotBlank() hint "view title must not be blank"
        maxLength(Limits.VIEW_TITLE_MAX_LENGTH) hint "view title must be shorter than ${Limits.VIEW_TITLE_MAX_LENGTH} characters"
    }

    SpaceView::description ifPresent {
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
}

private fun ValidationBuilder<SpaceUrlPreview>.validatePreview() {
    SpaceUrlPreview::image ifPresent {
        correctUrl()
    }
}

private fun ValidationBuilder<SpaceThemeName>.themeExists() =
    addConstraint("view theme not found") { themeName ->
        SpaceThemeDefinition.byName(themeName) != null
    }

private fun ValidationBuilder<Url>.correctUrl() =
    addConstraint("URL is invalid") { url ->
        isUrl(url)
    }