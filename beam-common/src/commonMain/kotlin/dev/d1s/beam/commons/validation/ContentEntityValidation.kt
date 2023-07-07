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
import dev.d1s.beam.commons.contententity.ContentEntityTypeDefinition
import io.konform.validation.Validation
import io.konform.validation.ValidationBuilder

internal val validateContentEntity: Validation<ContentEntity> = Validation {
    contentEntityTypeExists()
    validContentEntityParameters()
}

private fun ValidationBuilder<ContentEntity>.contentEntityTypeExists() =
    addConstraint("content entity must have valid type") { entity ->
        ContentEntityTypeDefinition.byName(entity.type) != null
    }

private fun ValidationBuilder<ContentEntity>.validContentEntityParameters() =
    addConstraint("content entity must have valid parameters") { entity ->
        val definition = ContentEntityTypeDefinition.byName(entity.type) ?: error("No definition for entity $entity")

        definition.parameters.forEach { parameterDefinition ->
            if (parameterDefinition.required) {
                entity.parameters[parameterDefinition.name] ?: return@addConstraint false
            }
        }

        true
    }