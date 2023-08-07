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
        required: Boolean = notRequired
    ): ContentEntityParameterDefinition =
        ContentEntityParameterDefinition(name, required)

    public companion object {

        private val definitions: List<ContentEntityTypeDefinition> = listOf(
            VoidContentEntityTypeDefinition,
            TextContentEntityTypeDefinition
        )

        public fun byName(name: ContentEntityTypeName): ContentEntityTypeDefinition? = definitions.find {
            it.name == name
        }
    }
}

public data object VoidContentEntityTypeDefinition : ContentEntityTypeDefinition(name = "void") {

    val height: ContentEntityParameterDefinition = parameter("height")
}

public data object TextContentEntityTypeDefinition : ContentEntityTypeDefinition(name = "text") {

    val value: ContentEntityParameterDefinition = parameter("value", required)

    val bold: ContentEntityParameterDefinition = parameter("bold")

    val italic: ContentEntityParameterDefinition = parameter("italic")

    val underline: ContentEntityParameterDefinition = parameter("underline")

    val strikethrough: ContentEntityParameterDefinition = parameter("strikethrough")

    val monospace: ContentEntityParameterDefinition = parameter("monospace")

    val url: ContentEntityParameterDefinition = parameter("url")
}