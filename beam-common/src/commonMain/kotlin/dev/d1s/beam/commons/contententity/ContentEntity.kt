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

import kotlinx.serialization.Serializable

public typealias ContentEntities = List<ContentEntity>

public typealias ContentEntityTypeName = String

public typealias ContentEntityParameterName = String
public typealias ContentEntityParameterValue = String
public typealias ContentEntityParameters = Map<ContentEntityParameterName, ContentEntityParameterValue>

@Serializable
public data class ContentEntity(
    val type: ContentEntityTypeName,
    val parameters: ContentEntityParameters
)

public operator fun ContentEntityParameters.get(definition: ContentEntityParameterDefinition): ContentEntityParameterValue? =
    this[definition.name].also {
        if (definition.required && it == null) {
            error("Parameter isn't specified: $definition")
        }
    }