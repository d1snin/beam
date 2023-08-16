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

package dev.d1s.beam.commons

import kotlinx.serialization.Serializable

public typealias SpaceIdentifier = String
public typealias SpaceId = SpaceIdentifier
public typealias SpaceSlug = SpaceIdentifier

public typealias SpaceToken = String

public sealed interface ModifiedRootSpace {

    public val metadata: Metadata

    public val view: ViewConfiguration
}

public sealed interface ModifiedSpace : ModifiedRootSpace {

    public val slug: SpaceSlug
}

public sealed interface IdentifiedSpace : ModifiedSpace {

    public val id: SpaceId

    public val createdAt: ModificationTime

    public val updatedAt: ModificationTime

    public val role: Role
}

@Serializable
public data class Space(
    override val id: SpaceId,
    override val createdAt: ModificationTime,
    override val updatedAt: ModificationTime,
    override val slug: SpaceSlug,
    override val metadata: Metadata,
    override val view: ViewConfiguration,
    override val role: Role,
) : IdentifiedSpace

@Serializable
public data class SpaceModification(
    override val slug: SpaceSlug,
    override val metadata: Metadata,
    override val view: ViewConfiguration
) : ModifiedSpace

@Serializable
public data class RootSpaceModification(
    override val metadata: Metadata,
    override val view: ViewConfiguration
) : ModifiedRootSpace

@Serializable
public data class SpaceWithToken(
    override val id: SpaceId,
    override val createdAt: ModificationTime,
    override val updatedAt: ModificationTime,
    override val slug: SpaceSlug,
    override val metadata: Metadata,
    override val view: ViewConfiguration,
    override val role: Role,
    public val token: SpaceToken
) : IdentifiedSpace