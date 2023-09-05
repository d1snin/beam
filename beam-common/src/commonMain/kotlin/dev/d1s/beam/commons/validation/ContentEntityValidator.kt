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
import io.konform.validation.ValidationBuilder

internal abstract class ContentEntityValidator<in D : ContentEntityTypeDefinition>(val global: Boolean = false) {

    constructor(definition: D) : this() {
        this.definition = definition
    }

    lateinit var definition: @UnsafeVariance D

    abstract fun ValidationBuilder<ContentEntity>.validate()

    fun ValidationBuilder<ContentEntity>.addTypedConstraint(errorMessage: String, test: (ContentEntity) -> Boolean) {
        addConstraint(errorMessage) { entity ->
            if (entity.type == definition.name) {
                test(entity)
            } else {
                true
            }
        }
    }

    companion object {

        val validators = listOf(
            ContentEntityParametersValidator,
            VoidContentEntityValidator,
            TextContentEntityValidator,
            ButtonLinkContentEntityValidator,
            SpaceContentEntityValidator
        )
    }
}