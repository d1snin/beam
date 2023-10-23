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

package dev.d1s.beam.client

import dev.d1s.beam.commons.*
import dev.d1s.beam.commons.contententity.*

@BuilderDsl
public class BlockModificationBuilder : ContentEntitiesBuilder() {

    public var index: BlockIndex? = null

    public var size: BlockSize? = null

    public var spaceId: SpaceIdentifier? = null

    private val metadataBuilder = MetadataBuilder()

    public fun metadata(key: MetadataKey, value: MetadataValue) {
        metadataBuilder.metadata(key, value)
    }

    public fun buildBlockModification(): BlockModification =
        BlockModification(
            index ?: error("Block index is undefined"),
            size ?: error("Block size is undefined"),
            buildContentEntities(),
            metadataBuilder.buildMetadata(),
            spaceId ?: error("Block space id is undefined")
        )
}

@BuilderDsl
public class ContentEntityBuilder {

    public var type: ContentEntityTypeDefinition? = null

    private var parameters: ContentEntityParameters = mapOf()

    public fun parameters(build: MutableMap<ContentEntityParameterName, ContentEntityParameterValue>.() -> Unit) {
        parameters = buildMap(build)
    }

    public fun parameters(vararg pairs: Pair<ContentEntityParameterName, ContentEntityParameterValue>) {
        parameters = mapOf(*pairs)
    }

    public fun buildContentEntity(): ContentEntity =
        ContentEntity(
            type?.name ?: error("Content entity type is undefined"),
            parameters
        )
}

@BuilderDsl
public open class ContentEntitiesBuilder {

    private val entities: MutableList<ContentEntity> = mutableListOf()

    public fun entity(build: ContentEntityBuilder.() -> Unit) {
        entities += ContentEntityBuilder().apply(build).buildContentEntity()
    }

    public fun buildContentEntities(): ContentEntities = entities
}

public fun ContentEntitiesBuilder.void(height: Int? = null) {
    entity {
        type = Void

        height?.let {
            parameters(
                Void.height.name to it.toString()
            )
        }
    }
}

public fun ContentEntitiesBuilder.text(value: String, heading: String? = null) {
    entity {
        type = Text

        parameters {
            put(Text.value.name, value)

            heading?.let {
                put(Text.heading.name, it)
            }
        }
    }
}

public fun ContentEntitiesBuilder.heading(value: String, level: TextContentEntityTypeDefinition.Heading) {
    text(value, heading = level.key)
}

public fun ContentEntitiesBuilder.firstHeading(value: String) {
    heading(value, level = TextContentEntityTypeDefinition.Heading.H1)
}

public fun ContentEntitiesBuilder.secondHeading(value: String) {
    heading(value, level = TextContentEntityTypeDefinition.Heading.H2)
}

public fun ContentEntitiesBuilder.thirdHeading(value: String) {
    heading(value, level = TextContentEntityTypeDefinition.Heading.H3)
}

public fun ContentEntitiesBuilder.buttonLink(
    text: String,
    icon: String? = null,
    url: String,
    style: ButtonLinkContentEntityTypeDefinition.Style? = null,
    width: Int? = null,
    height: Int? = null
) {
    entity {
        type = ButtonLink

        parameters {
            put(ButtonLink.text.name, text)

            icon?.let {
                put(ButtonLink.icon.name, it)
            }

            put(ButtonLink.url.name, url)

            style?.let {
                put(ButtonLink.style.name, it.identifier)
            }

            width?.let {
                put(ButtonLink.width.name, it.toString())
            }

            height?.let {
                put(ButtonLink.height.name, it.toString())
            }
        }
    }
}

public fun ContentEntitiesBuilder.fullWidthButtonLink(
    text: String,
    icon: String? = null,
    url: String,
    style: ButtonLinkContentEntityTypeDefinition.Style? = null,
    height: Int? = null
) {
    buttonLink(text, icon, url, style, width = 100, height)
}

public fun ContentEntitiesBuilder.space(
    identifier: SpaceIdentifier,
    fullWidth: Boolean? = null
) {
    entity {
        type = Space

        parameters {
            put(Space.identifier.name, identifier)

            fullWidth?.let {
                put(Space.fullWidth.name, it.toString())
            }
        }
    }
}

public fun ContentEntitiesBuilder.fullWidthSpace(identifier: SpaceIdentifier) {
    space(identifier, fullWidth = true)
}

public fun ContentEntitiesBuilder.image(
    url: String,
    description: String? = null,
    width: Int? = null,
    height: Int? = null
) {
    entity {
        type = Image

        parameters {
            put(Image.url.name, url)
            put(Image.url.name, url)

            description?.let {
                put(Image.description.name, it)
            }

            width?.let {
                put(Image.width.name, it.toString())
            }

            height?.let {
                put(Image.height.name, it.toString())
            }
        }
    }
}

public fun ContentEntitiesBuilder.fullWidthImage(
    url: String,
    description: String? = null,
    height: Int? = null
) {
    image(url, description, width = 100, height)
}

public fun ContentEntitiesBuilder.embed(
    url: String,
    document: String? = null,
    width: Int? = null,
    height: Int? = null
) {
    entity {
        type = Embed

        parameters {
            put(Embed.url.name, url)

            document?.let {
                put(Embed.document.name, it)
            }

            width?.let {
                put(Embed.width.name, it.toString())
            }

            height?.let {
                put(Embed.height.name, it.toString())
            }
        }
    }
}

public fun ContentEntitiesBuilder.fullWidthEmbed(
    url: String,
    document: String? = null,
    height: Int? = null
) {
    embed(url, document, width = 100, height)
}