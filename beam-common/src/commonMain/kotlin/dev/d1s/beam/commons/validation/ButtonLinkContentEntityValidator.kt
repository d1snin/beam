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

import dev.d1s.beam.commons.contententity.ButtonLinkContentEntityTypeDefinition
import dev.d1s.beam.commons.contententity.ContentEntity
import dev.d1s.beam.commons.contententity.get
import io.konform.validation.ValidationBuilder

internal object ButtonLinkContentEntityValidator :
    ContentEntityValidator<ButtonLinkContentEntityTypeDefinition>(ButtonLinkContentEntityTypeDefinition) {

    private val textBoundary = 1..100

    override fun ValidationBuilder<ContentEntity>.validate() {
        val validator = this@ButtonLinkContentEntityValidator

        requireCorrectBoundary(validator, definition.text, textBoundary, stringLengthMode = true)
        requireCorrectUrl(validator, definition.url)
        requireCorrectStyle()
        requireCorrectWidth(validator, definition.width)
        requireCorrectHeight(validator, definition.height)
    }

    private fun ValidationBuilder<ContentEntity>.requireCorrectStyle() {
        val message = "parameter '${definition.style.name}' is not one of the following values: ${
            ButtonLinkContentEntityTypeDefinition.Style.entries.joinToString(", ") { it.identifier }
        }"

        addTypedConstraint(message) { entity ->
            entity.parameters[definition.style]?.let { styleIdentifier ->
                val style = ButtonLinkContentEntityTypeDefinition.Style.byIdentifier(styleIdentifier)
                return@addTypedConstraint style != null
            }

            true
        }
    }
}