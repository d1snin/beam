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

package dev.d1s.beam.commons.contententity

import dev.d1s.beam.commons.util.lowercaseName

public abstract class CommonContentEntityTypeDefinition(name: String) : ContentEntityTypeDefinition(name) {

    public val alignment: ContentEntityParameterDefinition = parameter(CommonParameters.ALIGNMENT)

    public val collapsed: ContentEntityParameterDefinition = parameter(CommonParameters.COLLAPSED)

    protected fun widthParameter(
        required: Boolean = false,
        translatable: Boolean = false
    ): ContentEntityParameterDefinition = parameter("width", required, translatable)

    protected fun heightParameter(
        required: Boolean = false,
        translatable: Boolean = false
    ): ContentEntityParameterDefinition = parameter("height", required, translatable)

    protected fun urlParameter(
        required: Boolean = false,
        translatable: Boolean = true
    ): ContentEntityParameterDefinition = parameter("url", required, translatable)
}

public object CommonParameters {

    public const val ALIGNMENT: ContentEntityParameterName = "alignment"

    public const val COLLAPSED: ContentEntityParameterName = "collapsed"
}

public enum class Alignment {
    START, CENTER, END;

    public companion object {

        public val Default: Alignment = START

        public fun byName(name: String): Alignment? =
            entries.find {
                it.lowercaseName == name
            }
    }
}