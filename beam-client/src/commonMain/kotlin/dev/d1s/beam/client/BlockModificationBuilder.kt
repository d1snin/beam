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
public class BlockModificationBuilder {

    public var index: BlockIndex? = null

    public var size: BlockSize? = null

    public var metadata: Metadata = metadataOf()

    public var spaceId: SpaceIdentifier? = null

    private val entities: ContentEntitiesBuilder = ContentEntitiesBuilder()

    public fun entity(build: ContentEntityBuilder.() -> Unit) {
        entities.entity(build)
    }

    public fun build(): BlockModification =
        BlockModification(
            index ?: error("Block index is undefined"),
            size ?: error("Block size is undefined"),
            entities.build(),
            metadata,
            spaceId ?: error("Block space id is undefined")
        )
}

@BuilderDsl
public class ContentEntityBuilder {

    public var type: ContentEntityTypeDefinition? = null

    private var parameters: ContentEntityParameters = mapOf()

    public fun parameters(vararg pairs: Pair<ContentEntityParameterName, ContentEntityParameterValue>) {
        parameters = mapOf(*pairs)
    }

    public fun build(): ContentEntity =
        ContentEntity(
            type?.name ?: error("Content entity type is undefined"),
            parameters
        )
}

@BuilderDsl
public class ContentEntitiesBuilder {

    private val entities: MutableList<ContentEntity> = mutableListOf()

    public fun entity(build: ContentEntityBuilder.() -> Unit) {
        entities += ContentEntityBuilder().apply(build).build()
    }

    public fun build(): ContentEntities = entities
}

public val Void: ContentEntityTypeDefinition = VoidContentEntityTypeDefinition
public val Text: ContentEntityTypeDefinition = TextContentEntityTypeDefinition
public val ButtonLink: ContentEntityTypeDefinition = ButtonLinkContentEntityTypeDefinition
public val Space: ContentEntityTypeDefinition = SpaceContentEntityTypeDefinition
public val Image: ContentEntityTypeDefinition = ImageContentEntityTypeDefinition
public val Embed: ContentEntityTypeDefinition = EmbedContentEntityTypeDefinition
