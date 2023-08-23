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

public sealed class ContentEntityTypeDefinition(public val name: ContentEntityTypeName) {

    private val internalParameters = mutableListOf<ContentEntityParameterDefinition>()

    public val parameters: ContentEntityParameterDefinitions = internalParameters

    protected fun parameter(
        name: ContentEntityParameterName,
        required: Boolean = false,
        translatable: Boolean = false
    ): ContentEntityParameterDefinition =
        ContentEntityParameterDefinition(name, required, translatable)
}

public data object VoidContentEntityTypeDefinition : ContentEntityTypeDefinition(name = "void") {

    val height: ContentEntityParameterDefinition = parameter("height")
}

public data object TextContentEntityTypeDefinition : ContentEntityTypeDefinition(name = "text") {

    val value: ContentEntityParameterDefinition = parameter("value", required = true, translatable = true)

    val bold: ContentEntityParameterDefinition = parameter("bold")

    val italic: ContentEntityParameterDefinition = parameter("italic")

    val underline: ContentEntityParameterDefinition = parameter("underline")

    val strikethrough: ContentEntityParameterDefinition = parameter("strikethrough")

    val monospace: ContentEntityParameterDefinition = parameter("monospace")

    val heading: ContentEntityParameterDefinition = parameter("heading")

    val paragraph: ContentEntityParameterDefinition = parameter("paragraph")

    val secondary: ContentEntityParameterDefinition = parameter("secondary")

    val url: ContentEntityParameterDefinition = parameter("url", translatable = true)

    public enum class Heading(public val key: String) {
        H1("h1"), H2("h2"), H3("h3");

        public companion object {

            public fun byKey(key: String): Heading? =
                entries.find {
                    it.key == key
                }
        }
    }
}

private val definitions = listOf(
    VoidContentEntityTypeDefinition,
    TextContentEntityTypeDefinition
)

public fun definition(name: ContentEntityTypeName): ContentEntityTypeDefinition? =
    definitions.find {
        it.name == name
    }