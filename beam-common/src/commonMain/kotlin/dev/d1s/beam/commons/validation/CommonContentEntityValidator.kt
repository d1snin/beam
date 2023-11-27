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

import dev.d1s.beam.commons.contententity.AbstractContentEntity
import dev.d1s.beam.commons.contententity.Alignment
import dev.d1s.beam.commons.contententity.CommonContentEntityTypeDefinition
import dev.d1s.beam.commons.contententity.CommonParameters
import dev.d1s.beam.commons.util.lowercaseName
import io.konform.validation.ValidationBuilder

internal object CommonContentEntityValidator :
    ContentEntityValidator<CommonContentEntityTypeDefinition>(global = true) {

    override fun ValidationBuilder<AbstractContentEntity>.validate() {
        val validator = this@CommonContentEntityValidator

        requireCorrectAlignment()
        requireCorrectBoolean(validator, CommonParameters.COLLAPSED)
    }

    private fun ValidationBuilder<AbstractContentEntity>.requireCorrectAlignment() {
        val validator = this@CommonContentEntityValidator

        val alignments = Alignment.entries.map {
            it.lowercaseName
        }

        requireAnyText(validator, CommonParameters.ALIGNMENT, alignments)
    }
}