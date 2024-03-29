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

package dev.d1s.beam.commons.contententity

public sealed class ContentEntityTypeDefinition(public val name: ContentEntityTypeName) {

    private val internalParameters = mutableListOf<ContentEntityParameterDefinition>()

    public val parameters: ContentEntityParameterDefinitions = internalParameters

    protected fun parameter(
        name: ContentEntityParameterName,
        required: Boolean = false,
        translatable: Boolean = false
    ): ContentEntityParameterDefinition =
        ContentEntityParameterDefinition(name, required, translatable).also {
            internalParameters += it
        }
}

private val definitions = listOf(
    VoidContentEntityTypeDefinition,
    TextContentEntityTypeDefinition,
    ButtonLinkContentEntityTypeDefinition,
    AlertContentEntityTypeDefinition,
    SpaceContentEntityTypeDefinition,
    ImageContentEntityTypeDefinition,
    EmbedContentEntityTypeDefinition,
    FileContentEntityTypeDefinition
)

public fun definition(name: ContentEntityTypeName): ContentEntityTypeDefinition? =
    definitions.find {
        it.name == name
    }

public val Void: VoidContentEntityTypeDefinition = VoidContentEntityTypeDefinition
public val Text: TextContentEntityTypeDefinition = TextContentEntityTypeDefinition
public val ButtonLink: ButtonLinkContentEntityTypeDefinition = ButtonLinkContentEntityTypeDefinition
public val Alert: AlertContentEntityTypeDefinition = AlertContentEntityTypeDefinition
public val Space: SpaceContentEntityTypeDefinition = SpaceContentEntityTypeDefinition
public val Image: ImageContentEntityTypeDefinition = ImageContentEntityTypeDefinition
public val Embed: EmbedContentEntityTypeDefinition = EmbedContentEntityTypeDefinition
public val File: FileContentEntityTypeDefinition = FileContentEntityTypeDefinition