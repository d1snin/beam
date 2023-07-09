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

import dev.d1s.beam.commons.Paths
import dev.d1s.beam.commons.SpaceModification
import dev.d1s.beam.commons.validation.validateSpace
import dev.d1s.beam.server.configuration.DtoConverters
import dev.d1s.beam.server.entity.SpaceEntity
import dev.d1s.beam.server.exception.ForbiddenException
import dev.d1s.beam.server.service.AuthService
import dev.d1s.beam.server.service.SpaceService
import dev.d1s.beam.server.util.requiredIdParameter
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

internal class PutSpaceRoute : Route, KoinComponent {

    override val qualifier = named("put-space-route")

    private val spaceService by inject<SpaceService>()

    private val authService by inject<AuthService>()

    private val spaceModificationDtoConverter by inject<DtoConverter<SpaceEntity, SpaceModification>>(DtoConverters.SpaceModificationDtoConverterQualifier)

    override fun Routing.apply() {
        authenticate {
            put(Paths.PUT_SPACE) {
                if (authService.isSpaceModificationAllowed(call)) {
                    val body = call.receive<SpaceModification>()
                    validateSpace(body).orThrow()

                    val space = spaceModificationDtoConverter.convertToEntity(body)

                    val spaceIdentifier = call.requiredIdParameter
                    val updatedSpace = spaceService.updateSpace(spaceIdentifier, space).getOrThrow()

                    call.respond(updatedSpace.requiredDto)
                } else {
                    throw ForbiddenException()
                }
            }
        }
    }
}