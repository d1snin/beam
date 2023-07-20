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
import dev.d1s.beam.bundle.service.IndexService
import dev.d1s.exkt.ktor.server.koin.configuration.Route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class IndexRoute : Route, KoinComponent {

    override val qualifier = named("index-route")

    private val indexService by inject<IndexService>()

    override fun Routing.apply() {
        get(Paths.INDEX) {
            val path = call.request.path()
            val segments = path.split("/").filterNot { it.isBlank() }

            val resolvedSpace = resolveSpace(segments)
            call.respondHtml(resolvedSpace)
        }
    }

    private suspend fun resolveSpace(pathSegments: List<String>) =
        if (pathSegments.size == 1) {
            val spaceIdentifier = pathSegments.first()
            indexService.resolveSpace(spaceIdentifier)
        } else {
            indexService.resolveSpace(spaceIdentifier = null)
        }

    private suspend fun ApplicationCall.respondHtml(resolvedSpace: ResolvedSpace) {
        val contentType = ContentType.Text.Html.withCharset(Charsets.UTF_8)

        respondTextWriter(contentType) {
            append("<!DOCTYPE html>\n")
            append(resolvedSpace.html)
        }
    }
}