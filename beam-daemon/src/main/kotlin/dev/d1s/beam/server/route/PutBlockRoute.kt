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

package dev.d1s.beam.server.route

import dev.d1s.beam.commons.BlockModification
import dev.d1s.beam.commons.Paths
import dev.d1s.beam.commons.validation.validateBlock
import dev.d1s.beam.server.configuration.DtoConverters
import dev.d1s.beam.server.configuration.jwtSubject
import dev.d1s.beam.server.entity.BlockEntity
import dev.d1s.beam.server.service.AuthService
import dev.d1s.beam.server.service.BlockService
import dev.d1s.beam.server.util.requiredIdParameter
import dev.d1s.beam.server.util.respondForbidden
import dev.d1s.beam.server.validation.orThrow
import dev.d1s.exkt.dto.DtoConverter
import dev.d1s.exkt.dto.requiredDto
import dev.d1s.exkt.ktor.server.koin.configuration.Route
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

internal class PutBlockRoute : Route, KoinComponent {

    override val qualifier = named("put-block-route")

    private val blockService by inject<BlockService>()

    private val authService by inject<AuthService>()

    private val blockModificationDtoConverter by inject<DtoConverter<BlockEntity, BlockModification>>(DtoConverters.BlockModificationDtoConverterQualifier)

    override fun Routing.apply() {
        authenticate {
            put(Paths.PUT_BLOCK) {
                val body = call.receive<BlockModification>()
                validateBlock(body).orThrow()

                val blockModificationAllowed = authService.isBlockModificationAllowed(call)
                val spaceModificationAllowed =
                    authService.isSpaceModificationAllowed(call.jwtSubject, body.spaceId).getOrThrow()

                if (blockModificationAllowed && spaceModificationAllowed) {
                    val block = blockModificationDtoConverter.convertToEntity(body)

                    val blockId = call.requiredIdParameter
                    val updatedBlock = blockService.updateBlock(blockId, block).getOrThrow()

                    call.respond(updatedBlock.requiredDto)
                } else {
                    call.respondForbidden()
                }
            }
        }
    }
}