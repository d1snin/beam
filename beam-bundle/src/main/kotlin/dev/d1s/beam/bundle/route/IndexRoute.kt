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

package dev.d1s.beam.bundle.route

import dev.d1s.beam.bundle.entity.ResolvedSpace
import dev.d1s.beam.bundle.entity.SpaceRequest
import dev.d1s.beam.bundle.service.IndexService
import dev.d1s.beam.bundle.util.respondHtml
import dev.d1s.beam.client.BeamClient
import dev.d1s.exkt.ktor.server.koin.configuration.Route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import org.lighthousegames.logging.logging

class IndexRoute : Route, KoinComponent {

    override val qualifier = named("index-route")

    private val indexService by inject<IndexService>()

    private val client by inject<BeamClient>()

    private val logger = logging()

    override fun Routing.apply() {
        get(Paths.Index) {
            logger.d {
                "Handling ${Paths.Index}"
            }

            val resolvedSpace = resolveSpace(call)

            logger.d {
                "Responding resolved space..."
            }

            call.respondHtml(resolvedSpace.html)
        }
    }

    private suspend fun resolveSpace(call: ApplicationCall): ResolvedSpace {
        call.log()

        val spaceIdentifier = client.resolver.resolveIdentifier(call.url())

        logger.d {
            "Space identifier: $spaceIdentifier"
        }

        val request = SpaceRequest(spaceIdentifier, call)

        return indexService.resolveSpace(request)
    }

    private fun ApplicationCall.log() {
        logger.i {
            val origin = request.origin

            val host = origin.remoteHost
            val port = origin.remotePort

            val userAgent = request.userAgent() ?: "no user agent"

            "New space request on ${url()} from $host:$port ($userAgent)"
        }
    }
}