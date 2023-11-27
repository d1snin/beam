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

package dev.d1s.beam.daemon.configuration

import dev.d1s.beam.commons.*
import dev.d1s.beam.commons.contententity.ContentEntity
import dev.d1s.beam.commons.contententity.ContentEntityModification
import dev.d1s.beam.daemon.converter.*
import dev.d1s.beam.daemon.entity.BlockEntity
import dev.d1s.beam.daemon.entity.RowEntity
import dev.d1s.beam.daemon.entity.SpaceEntity
import dev.d1s.beam.daemon.entity.TranslationEntity
import dev.d1s.exkt.dto.DtoConverter
import dev.d1s.exkt.ktor.server.koin.configuration.ApplicationConfigurer
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named

object DtoConverters : ApplicationConfigurer {

    val BlockDtoConverterQualifier = named("block-dto-converter")
    val BlockModificationDtoConverterQualifier = named("block-modification-dto-converter")

    val ContentEntityModificationDtoConverter = named("content-entity-modification-dto-converter")

    val RowDtoConverterQualifier = named("row-dto-converter")
    val RowModificationDtoConverterQualifier = named("row-modification-dto-converter")

    val SpaceDtoConverterQualifier = named("space-dto-converter")
    val SpaceModificationDtoConverterQualifier = named("space-modification-dto-converter")
    val RootSpaceModificationDtoConverterQualifier = named("root-space-modification-dto-converter")

    val TranslationDtoConverterQualifier = named("translation-dto-converter")
    val TranslationModificationDtoConverterQualifier = named("translation-modification-dto-converter")

    override fun Application.configure(module: Module, config: ApplicationConfig) {
        module.apply {
            singleOf<DtoConverter<BlockEntity, Block>>(::BlockDtoConverter) {
                qualifier = BlockDtoConverterQualifier
            }

            singleOf<DtoConverter<BlockEntity, BlockModification>>(::BlockModificationDtoConverter) {
                qualifier = BlockModificationDtoConverterQualifier
            }

            singleOf<DtoConverter<ContentEntity, ContentEntityModification>>(::ContentEntityModificationDtoConverter) {
                qualifier = ContentEntityModificationDtoConverter
            }

            singleOf<DtoConverter<RowEntity, Row>>(::RowDtoConverter) {
                qualifier = RowDtoConverterQualifier
            }

            singleOf<DtoConverter<RowEntity, RowModification>>(::RowModificationDtoConverter) {
                qualifier = RowModificationDtoConverterQualifier
            }

            singleOf<DtoConverter<SpaceEntity, Space>>(::SpaceDtoConverter) {
                qualifier = SpaceDtoConverterQualifier
            }

            singleOf<DtoConverter<SpaceEntity, SpaceModification>>(::SpaceModificationDtoConverter) {
                qualifier = SpaceModificationDtoConverterQualifier
            }

            singleOf<DtoConverter<SpaceEntity, RootSpaceModification>>(::RootSpaceModificationDtoConverter) {
                qualifier = RootSpaceModificationDtoConverterQualifier
            }

            singleOf<DtoConverter<TranslationEntity, Translation>>(::TranslationDtoConverter) {
                qualifier = TranslationDtoConverterQualifier
            }

            singleOf<DtoConverter<TranslationEntity, TranslationModification>>(::TranslationModificationDtoConverter) {
                qualifier = TranslationModificationDtoConverterQualifier
            }
        }
    }
}