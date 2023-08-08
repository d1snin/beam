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
import dev.d1s.beam.commons.contententity.VoidContentEntityTypeDefinition
import dev.d1s.beam.commons.contententity.get
import io.konform.validation.ValidationBuilder

internal object VoidContentEntityValidator :
    ContentEntityValidator<VoidContentEntityTypeDefinition>(VoidContentEntityTypeDefinition) {

    private val boundary = 1..1200

    override fun ValidationBuilder<ContentEntity>.validate() {
        requireInt()
        requireBoundary()
    }

    private fun ValidationBuilder<ContentEntity>.requireInt() {
        addTypedConstraint("parameter ${definition.height.name} is not an integer") { entity ->
            entity.height()?.let {
                it.toIntOrNull() != null
            } ?: true
        }
    }

    private fun ValidationBuilder<ContentEntity>.requireBoundary() {
        addTypedConstraint("parameter ${definition.height.name} is not in boundary $boundary") { entity ->
            entity.height()?.let {
                val height = it.toIntOrNull()
                height in boundary
            } ?: true
        }
    }

    private fun ContentEntity.height() = parameters[definition.height]
}