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

package dev.d1s.beam.daemon.database

import dev.d1s.beam.commons.BlockSize
import dev.d1s.beam.commons.Metadata
import dev.d1s.beam.commons.contententity.ContentEntities
import dev.d1s.beam.daemon.entity.BlockEntity
import dev.d1s.exkt.ktorm.UuidIdentifiedEntities
import org.ktorm.jackson.json
import org.ktorm.schema.enum
import org.ktorm.schema.int
import org.ktorm.schema.uuid

@Suppress("unused")
internal object Blocks : UuidIdentifiedEntities<BlockEntity>(tableName = "block") {

    val index = int("index").bindTo {
        it.index
    }

    val size = enum<BlockSize>("size").bindTo {
        it.size
    }

    val entities = json<ContentEntities>("entities").bindTo {
        it.entities
    }

    val metadata = json<Metadata>("metadata").bindTo {
        it.metadata
    }

    val spaceId = uuid("space_id").references(Spaces) {
        it.space
    }
}