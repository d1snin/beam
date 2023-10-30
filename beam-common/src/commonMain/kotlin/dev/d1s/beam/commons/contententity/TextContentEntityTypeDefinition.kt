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

public data object TextContentEntityTypeDefinition : CommonContentEntityTypeDefinition(name = "text") {

    val value: ContentEntityParameterDefinition = parameter("value", required = true, translatable = true)

    val heading: ContentEntityParameterDefinition = parameter("heading")

    public enum class Heading {
        H1, H2, H3;

        public companion object {

            public fun byName(name: String): Heading? =
                entries.find {
                    it.lowercaseName == name
                }
        }
    }
}