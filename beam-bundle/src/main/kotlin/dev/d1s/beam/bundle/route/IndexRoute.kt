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

package dev.d1s.beam.bundle.route

import dev.d1s.beam.bundle.entity.ResolvedSpace
import dev.d1s.beam.bundle.entity.SpaceRequest
import dev.d1s.beam.bundle.service.IndexService
import dev.d1s.exkt.ktor.server.koin.configuration.Route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import org.lighthousegames.logging.logging

class IndexRoute : Route, KoinComponent {

    override val qualifier = named("index-route")

    private val indexService by inject<IndexService>()

    private val logger = logging()

    override fun Routing.apply() {
        get(Paths.INDEX) {
            logger.d {
                "Handling ${Paths.INDEX}"
            }

            val path = call.request.path()
            val segments = path.split("/").filterNot { it.isBlank() }

            logger.d {
                "Path: $path"
            }

            val resolvedSpace = resolveSpace(segments, call)

            logger.d {
                "Responding resolved space..."
            }

            call.respondHtml(resolvedSpace)
        }
    }

    private suspend fun resolveSpace(pathSegments: List<String>, call: ApplicationCall): ResolvedSpace {
        val spaceIdentifier = when (pathSegments.size) {
            0 -> "root"
            1 -> pathSegments.first()
            else -> null
        }

        logger.d {
            "Space identifier: $spaceIdentifier"
        }

        val request = SpaceRequest(spaceIdentifier, call)

        return indexService.resolveSpace(request)
    }

    private suspend fun ApplicationCall.respondHtml(resolvedSpace: ResolvedSpace) {
        val contentType = ContentType.Text.Html.withCharset(Charsets.UTF_8)

        respondTextWriter(contentType) {
            append("<!DOCTYPE html>\n")
            append(resolvedSpace.html)
        }
    }
}