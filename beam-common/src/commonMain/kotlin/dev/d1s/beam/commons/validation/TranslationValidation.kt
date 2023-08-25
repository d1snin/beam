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

import dev.d1s.beam.commons.AbstractTranslation
import dev.d1s.beam.commons.TextLocation
import dev.d1s.beam.commons.TranslatedText
import dev.d1s.exkt.konform.matches
import io.konform.validation.Validation

public val validateTranslation: Validation<AbstractTranslation> = Validation {
    AbstractTranslation::languageCode {
        matches(Regex.LanguageCode)
    }

    AbstractTranslation::translations onEach {
        Map.Entry<TextLocation, TranslatedText>::key {
            matches(Regex.UnwrappedTextLocation)
        }
    }
}