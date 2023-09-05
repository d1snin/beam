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

import dev.d1s.beam.commons.contententity.ContentEntity
import dev.d1s.beam.commons.contententity.ContentEntityParameterDefinition
import dev.d1s.beam.commons.contententity.TextContentEntityTypeDefinition
import dev.d1s.beam.commons.contententity.get
import io.konform.validation.ValidationBuilder

internal object TextContentEntityValidator :
    ContentEntityValidator<TextContentEntityTypeDefinition>(TextContentEntityTypeDefinition) {

    override fun ValidationBuilder<ContentEntity>.validate() {
        requireNotBlankValue()

        requireBoolean(definition.bold)
        requireBoolean(definition.italic)
        requireBoolean(definition.underline)
        requireBoolean(definition.strikethrough)
        requireBoolean(definition.monospace)
        requireBoolean(definition.paragraph)
        requireBoolean(definition.secondary)

        requireHeading()

        requireCorrectUrl(definition.url)

        requireNoCollision(definition.heading, definition.paragraph)
    }

    private fun ValidationBuilder<ContentEntity>.requireNotBlankValue() {
        addTypedConstraint("parameter '${definition.value.name}' must not be blank") { entity ->
            val value = entity.parameters[definition.value]

            value?.isBlank() != true
        }
    }

    private fun ValidationBuilder<ContentEntity>.requireBoolean(parameterDefinition: ContentEntityParameterDefinition) {
        addTypedConstraint("parameter '${parameterDefinition.name}' must be 'true' or 'false'") { entity ->
            entity.parameters[parameterDefinition]?.let {
                it.toBooleanStrictOrNull() ?: return@addTypedConstraint false
            }

            true
        }
    }

    private fun ValidationBuilder<ContentEntity>.requireHeading() {
        val headings = TextContentEntityTypeDefinition.Heading.entries.joinToString(", ") { it.key }

        addTypedConstraint("parameter '${definition.heading.name}' must be one of the following: $headings") { entity ->
            entity.parameters[definition.heading]?.let { heading ->
                return@addTypedConstraint TextContentEntityTypeDefinition.Heading.byKey(heading) != null
            }

            true
        }
    }

    private fun ValidationBuilder<ContentEntity>.requireNoCollision(vararg collidingParameters: ContentEntityParameterDefinition) {
        val parameterNames = collidingParameters.joinToString(", ") {
            "'${it.name}'"
        }

        addTypedConstraint("parameters $parameterNames are colliding") { entity ->
            var oneSpecified = false

            collidingParameters.forEach { collidingParameter ->
                val value = entity.parameters[collidingParameter]

                value?.let {
                    if (oneSpecified) {
                        return@addTypedConstraint false
                    } else {
                        oneSpecified = true
                    }
                }
            }

            return@addTypedConstraint true
        }
    }
}