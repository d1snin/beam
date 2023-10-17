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

import dev.d1s.beam.commons.Block
import kotlinx.serialization.Serializable

public typealias ContentEntities = List<ContentEntity>

public typealias ContentEntityTypeName = String

public typealias ContentEntityParameterName = String
public typealias ContentEntityParameterValue = String
public typealias ContentEntityParameters = Map<ContentEntityParameterName, ContentEntityParameterValue>
public typealias ContentEntityParameter = Map.Entry<ContentEntityParameterName, ContentEntityParameterValue>

@Serializable
public data class ContentEntity(
    val type: ContentEntityTypeName,
    val parameters: ContentEntityParameters
)

public operator fun ContentEntityParameters.get(definition: ContentEntityParameterDefinition): ContentEntityParameterValue? =
    this[definition.name]

public fun ContentEntity.isFirstIn(block: Block): Boolean =
    this === block.entities.firstOrNull()

public fun ContentEntity.isLastIn(block: Block): Boolean =
    this === block.entities.lastOrNull()

public fun ContentEntities.isLastIn(block: Block): Boolean =
    this.lastOrNull()?.equals(block.entities.lastOrNull()) == true