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

import dev.d1s.beam.commons.contententity.ContentEntities
import kotlinx.serialization.Serializable

public typealias BlockId = String

public typealias BlockIndex = Int

public sealed interface AbstractBlock {

    public val row: RowIndex

    public val index: BlockIndex?

    public val size: BlockSize

    public val entities: ContentEntities

    public val metadata: Metadata

    public val spaceId: SpaceId
}

@Serializable
public data class Block(
    val id: BlockId,
    override val row: RowIndex,
    override val index: BlockIndex,
    override val size: BlockSize,
    override val entities: ContentEntities,
    override val metadata: Metadata,
    override val spaceId: SpaceId
) : AbstractBlock

@Serializable
public data class BlockModification(
    override val row: RowIndex,
    override val index: BlockIndex?,
    override val size: BlockSize,
    override val entities: ContentEntities,
    override val metadata: Metadata,
    override val spaceId: SpaceId
) : AbstractBlock

public enum class BlockSize {
    SMALL, MEDIUM, LARGE, EXTRA_LARGE, HALF;

    public val level: Int
        get() = if (this == HALF) {
            HALF_LEVEL
        } else {
            ordinal + 1
        }

    public companion object {

        public const val HALF_LEVEL: Int = 2
    }
}