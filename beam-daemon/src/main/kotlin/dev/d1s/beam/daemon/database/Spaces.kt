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

package dev.d1s.beam.daemon.database

import dev.d1s.beam.commons.Metadata
import dev.d1s.beam.commons.Role
import dev.d1s.beam.commons.SpaceView
import dev.d1s.beam.daemon.entity.SpaceEntity
import dev.d1s.exkt.ktorm.UuidIdentifiedAndModificationTimestampAwareEntities
import org.ktorm.jackson.json
import org.ktorm.schema.enum
import org.ktorm.schema.text

object Spaces : UuidIdentifiedAndModificationTimestampAwareEntities<SpaceEntity>(tableName = "space") {

    val slug = text("slug").bindTo {
        it.slug
    }

    val metadata = json<Metadata>("metadata").bindTo {
        it.metadata
    }

    val view = json<SpaceView>("view").bindTo {
        it.view
    }

    val role = enum<Role>("role").bindTo {
        it.role
    }
}