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

package dev.d1s.beam.server.configuration

import dev.d1s.beam.commons.Block
import dev.d1s.beam.commons.BlockModification
import dev.d1s.beam.commons.Space
import dev.d1s.beam.commons.SpaceModification
import dev.d1s.beam.server.converter.BlockDtoConverter
import dev.d1s.beam.server.converter.BlockModificationDtoConverter
import dev.d1s.beam.server.converter.SpaceDtoConverter
import dev.d1s.beam.server.converter.SpaceModificationDtoConverter
import dev.d1s.beam.server.entity.BlockEntity
import dev.d1s.beam.server.entity.SpaceEntity
import dev.d1s.exkt.dto.DtoConverter
import dev.d1s.exkt.ktor.server.koin.configuration.ApplicationConfigurer
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named

internal object DtoConverters : ApplicationConfigurer {

    val BlockDtoConverterQualifier = named("block-dto-converter")
    val BlockModificationDtoConverterQualifier = named("block-modification-dto-converter")

    val SpaceDtoConverterQualifier = named("space-dto-converter")
    val SpaceModificationDtoConverterQualifier = named("space-modification-dto-converter")

    override fun Application.configure(module: Module, config: ApplicationConfig) {
        module.singleOf<DtoConverter<BlockEntity, Block>>(::BlockDtoConverter) {
            qualifier = BlockDtoConverterQualifier
        }

        module.singleOf<DtoConverter<BlockEntity, BlockModification>>(::BlockModificationDtoConverter) {
            qualifier = BlockModificationDtoConverterQualifier
        }

        module.singleOf<DtoConverter<SpaceEntity, Space>>(::SpaceDtoConverter) {
            qualifier = SpaceDtoConverterQualifier
        }

        module.singleOf<DtoConverter<SpaceEntity, SpaceModification>>(::SpaceModificationDtoConverter) {
            qualifier = SpaceModificationDtoConverterQualifier
        }
    }
}