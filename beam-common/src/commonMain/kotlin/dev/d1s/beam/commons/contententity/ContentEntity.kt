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

import dev.d1s.beam.commons.Metadata
import kotlinx.serialization.Serializable

public typealias AbstractContentEntities = List<AbstractContentEntity>
public typealias ContentEntities = List<ContentEntity>
public typealias ContentEntityModifications = List<ContentEntityModification>

public typealias ContentEntityTypeName = String

public typealias ContentEntityParameterName = String
public typealias ContentEntityParameterValue = String
public typealias ContentEntityParameters = Map<ContentEntityParameterName, ContentEntityParameterValue>
public typealias ContentEntityParameter = Map.Entry<ContentEntityParameterName, ContentEntityParameterValue>

public sealed interface AbstractContentEntity {

    public val type: ContentEntityTypeName

    public val parameters: ContentEntityParameters
}

@Serializable
public data class ContentEntity(
    override val type: ContentEntityTypeName,
    override val parameters: ContentEntityParameters,
    val metadata: Metadata
) : AbstractContentEntity

@Serializable
public data class ContentEntityModification(
    override val type: ContentEntityTypeName,
    override val parameters: ContentEntityParameters
) : AbstractContentEntity

public operator fun ContentEntityParameters.get(definition: ContentEntityParameterDefinition): ContentEntityParameterValue? =
    this[definition.name]

public fun ContentEntity.isFirstIn(batch: ContentEntities): Boolean =
    this === batch.firstOrNull()

public fun ContentEntity.isLastIn(batch: ContentEntities): Boolean =
    this === batch.lastOrNull()

public fun ContentEntities.isLastIn(batch: ContentEntities): Boolean =
    this.lastOrNull()?.equals(batch.lastOrNull()) == true