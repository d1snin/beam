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
import dev.d1s.beam.commons.contententity.ContentEntityParameterName
import dev.d1s.beam.commons.contententity.get
import io.konform.validation.ValidationBuilder

private val widthBoundary = 1..100
private val heightBoundary = 1..1200

internal fun ValidationBuilder<ContentEntity>.requireCorrectWidth(
    validator: ContentEntityValidator<*>,
    parameterDefinition: ContentEntityParameterDefinition
) {
    requireCorrectInt(validator, parameterDefinition)
    requireCorrectBoundary(validator, parameterDefinition, widthBoundary)
}

internal fun ValidationBuilder<ContentEntity>.requireCorrectHeight(
    validator: ContentEntityValidator<*>,
    parameterDefinition: ContentEntityParameterDefinition
) {
    requireCorrectInt(validator, parameterDefinition)
    requireCorrectBoundary(validator, parameterDefinition, heightBoundary)
}

internal fun ValidationBuilder<ContentEntity>.requireCorrectUrl(
    validator: ContentEntityValidator<*>,
    parameterDefinition: ContentEntityParameterDefinition
) {
    with(validator) {
        addTypedConstraint("parameter '${parameterDefinition.name}' must be a correct url") { entity ->
            entity.parameters[parameterDefinition]?.let { url ->
                return@addTypedConstraint isUrl(url)
            }

            true
        }
    }
}

internal fun ValidationBuilder<ContentEntity>.requireCorrectInt(
    validator: ContentEntityValidator<*>,
    parameterDefinition: ContentEntityParameterDefinition
) {
    with(validator) {
        addTypedConstraint("parameter '${parameterDefinition.name}' is not an integer") { entity ->
            entity.parameters[parameterDefinition]?.let {
                it.toIntOrNull() != null
            } ?: true
        }
    }
}

internal fun ValidationBuilder<ContentEntity>.requireCorrectBoundary(
    validator: ContentEntityValidator<*>,
    parameterDefinition: ContentEntityParameterDefinition,
    boundary: IntRange,
    stringLengthMode: Boolean = false
) {
    with(validator) {
        addTypedConstraint("parameter '${parameterDefinition.name}' is not within its boundary $boundary") { entity ->
            entity.parameters[parameterDefinition]?.let {
                val value = if (stringLengthMode) {
                    it.length
                } else {
                    it.toIntOrNull()
                }

                value in boundary
            } ?: true
        }
    }
}

internal fun ValidationBuilder<ContentEntity>.requireNotBlankText(
    validator: ContentEntityValidator<*>,
    parameterDefinition: ContentEntityParameterDefinition
) {
    with(validator) {
        addTypedConstraint("parameter '${parameterDefinition.name}' must not be blank") { entity ->
            val value = entity.parameters[parameterDefinition]

            value?.isBlank() != true
        }
    }
}

internal fun ValidationBuilder<ContentEntity>.requireCorrectBoolean(
    validator: ContentEntityValidator<*>,
    parameterName: ContentEntityParameterName
) {
    with(validator) {
        addTypedConstraint("parameter '${parameterName} must be 'true' or 'false'") { entity ->
            entity.parameters[parameterName]?.let {
                it.toBooleanStrictOrNull() ?: return@addTypedConstraint false
            }

            true
        }
    }
}

internal fun ValidationBuilder<ContentEntity>.requireCorrectBoolean(
    validator: ContentEntityValidator<*>,
    parameterDefinition: ContentEntityParameterDefinition
) {
    requireCorrectBoolean(validator, parameterDefinition.name)
}