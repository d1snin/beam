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

package dev.d1s.beam.daemon.entity

import dev.d1s.beam.commons.BlockIndex
import dev.d1s.beam.commons.BlockSize
import dev.d1s.beam.commons.Metadata
import dev.d1s.beam.commons.RowIndex
import dev.d1s.beam.commons.contententity.ContentEntities
import dev.d1s.exkt.ktorm.UuidIdentified
import org.ktorm.entity.Entity

typealias BlockEntities = List<BlockEntity>

interface BlockEntity : UuidIdentified<BlockEntity> {

    var row: RowIndex

    var index: BlockIndex?

    var size: BlockSize

    var entities: ContentEntities

    var metadata: Metadata

    var space: SpaceEntity

    companion object : Entity.Factory<BlockEntity>()
}

val BlockEntity.asString
    get() = "BlockEntity{row = $row, index = $index, size = $size, entities = $entities, metadata = $metadata}"