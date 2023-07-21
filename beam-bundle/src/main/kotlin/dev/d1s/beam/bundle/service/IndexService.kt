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

package dev.d1s.beam.bundle.service

import dev.d1s.beam.bundle.entity.ResolvedSpace
import dev.d1s.beam.bundle.entity.SpaceRequest
import dev.d1s.beam.bundle.html.IndexHtmlRenderer
import dev.d1s.beam.bundle.html.RenderParameters
import dev.d1s.beam.bundle.html.SpaceUrlPreview
import dev.d1s.beam.bundle.response.Defaults
import dev.d1s.beam.client.PublicBeamClient
import dev.d1s.beam.commons.Space
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.server.request.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface IndexService {

    suspend fun resolveSpace(request: SpaceRequest): ResolvedSpace
}

class DefaultIndexService : IndexService, KoinComponent {

    private val beamClient by inject<PublicBeamClient>()

    private val renderer by inject<IndexHtmlRenderer>()

    override suspend fun resolveSpace(request: SpaceRequest): ResolvedSpace {
        val spaceIdentifier = request.spaceIdentifier ?: return notFoundSpace()

        val spaceResult = beamClient.getSpace(spaceIdentifier)

        spaceResult.onFailure {
            return if (it is ClientRequestException && it.response.status == HttpStatusCode.NotFound) {
                notFoundSpace()
            } else {
                notFoundSpace("Beam Space not available")
            }
        }

        val space = spaceResult.getOrThrow()

        return foundSpace(space, request)
    }

    private fun notFoundSpace(message: String = "Beam Space not found"): ResolvedSpace {
        val parameters = RenderParameters(
            title = message,
            description = null,
            themeColor = null,
            urlPreview = null
        )

        val html = renderer.renderIndex(parameters)

        return ResolvedSpace(space = null, html)
    }

    private fun foundSpace(space: Space, request: SpaceRequest): ResolvedSpace {
        val view = space.view

        val title = view.title ?: Defaults.TITLE
        val description = view.description ?: Defaults.DESCRIPTION

        val url = URLBuilder(request.call.request.uri).apply {
            set {
                parameters.clear()
            }
        }.build().toString()

        val urlPreview = SpaceUrlPreview(
            url,
            siteName = Defaults.SITE_NAME,
            title,
            description,
            image = view.icon ?: Defaults.ICON
        )

        val parameters = RenderParameters(
            title,
            description,
            themeColor = Defaults.FOUND_SPACE_THEME_COLOR,
            urlPreview
        )

        val html = renderer.renderIndex(parameters)

        return ResolvedSpace(space, html)
    }
}