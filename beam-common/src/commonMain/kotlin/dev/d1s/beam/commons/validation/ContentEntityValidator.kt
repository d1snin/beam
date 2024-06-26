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
import dev.d1s.beam.commons.contententity.ContentEntityTypeDefinition
import io.konform.validation.ValidationBuilder

internal abstract class ContentEntityValidator<in D : ContentEntityTypeDefinition>(val global: Boolean = false) {

    constructor(definition: D) : this() {
        this.definition = definition
    }

    var definition: @UnsafeVariance D? = null

    val requiredDefinition
        get() = requireNotNull(definition) {
            "Content entity definition for validator is not set"
        }

    abstract fun ValidationBuilder<AbstractContentEntity>.validate()

    fun ValidationBuilder<AbstractContentEntity>.addTypedConstraint(
        errorMessage: String,
        test: (AbstractContentEntity) -> Boolean
    ) {
        addConstraint(errorMessage) { entity ->
            val entityType = entity.type

            if (entityType == (definition?.name ?: entityType)) {
                test(entity)
            } else {
                true
            }
        }
    }

    companion object {

        val validators = listOf(
            ContentEntityParametersValidator,
            CommonContentEntityValidator,

            VoidContentEntityValidator,
            TextContentEntityValidator,
            ButtonLinkContentEntityValidator,
            AlertContentEntityValidator,
            SpaceContentEntityValidator,
            ImageContentEntityValidator,
            EmbedContentEntityValidator,
            FileContentEntityValidator
        )
    }
}