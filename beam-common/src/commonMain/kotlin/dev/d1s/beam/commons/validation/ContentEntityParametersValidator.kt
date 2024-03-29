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

package dev.d1s.beam.commons.validation

import dev.d1s.beam.commons.contententity.AbstractContentEntity
import dev.d1s.beam.commons.contententity.definition
import io.konform.validation.ValidationBuilder

internal object ContentEntityParametersValidator : ContentEntityValidator<Nothing>(global = true) {

    override fun ValidationBuilder<AbstractContentEntity>.validate() {
        requireDefinition()
        requireParameters()
    }

    private fun ValidationBuilder<AbstractContentEntity>.requireDefinition() {
        addConstraint("content entity does not exist") { entity ->
            entity.definition() != null
        }
    }

    private fun ValidationBuilder<AbstractContentEntity>.requireParameters() {
        addConstraint("missing parameters") { entity ->
            val definition = entity.definition()

            definition?.parameters?.forEach { parameterDefinition ->
                if (parameterDefinition.required) {
                    val name = parameterDefinition.name
                    entity.parameters[name] ?: return@addConstraint false
                }
            }

            true
        }
    }

    private fun AbstractContentEntity.definition() = definition(type)
}