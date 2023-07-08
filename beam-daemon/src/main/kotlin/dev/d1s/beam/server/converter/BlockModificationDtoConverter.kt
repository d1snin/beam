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

package dev.d1s.beam.server.converter

import dev.d1s.beam.commons.BlockModification
import dev.d1s.beam.server.entity.BlockEntity
import dev.d1s.beam.server.service.SpaceService
import dev.d1s.exkt.dto.DtoConverter
import dev.d1s.exkt.dto.entity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class BlockModificationDtoConverter : DtoConverter<BlockEntity, BlockModification>, KoinComponent {

    private val spaceService by inject<SpaceService>()

    override suspend fun convertToEntity(dto: BlockModification) =
        BlockEntity {
            index = dto.index
            size = dto.size
            entities = dto.entities
            metadata = dto.metadata
            space = spaceService.getSpace(dto.spaceId).getOrThrow().entity
        }
}