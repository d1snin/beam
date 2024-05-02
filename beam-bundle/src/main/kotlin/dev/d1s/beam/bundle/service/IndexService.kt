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

package dev.d1s.beam.bundle.service

import dev.d1s.beam.bundle.entity.ResolvedSpace
import dev.d1s.beam.bundle.entity.SpaceRequest
import dev.d1s.beam.bundle.html.IndexHtmlRenderer
import dev.d1s.beam.bundle.html.RenderParameters
import dev.d1s.beam.bundle.html.UrlPreviewMetaTags
import dev.d1s.beam.bundle.response.Defaults
import dev.d1s.beam.client.BeamClient
import dev.d1s.beam.commons.LanguageCode
import dev.d1s.beam.commons.Space
import dev.d1s.beam.commons.SpaceFavicon
import dev.d1s.beam.commons.SpaceUrlPreview
import io.ktor.client.plugins.*
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging

interface IndexService {

    suspend fun resolveSpace(request: SpaceRequest): ResolvedSpace
}

class DefaultIndexService : IndexService, KoinComponent {

    private val client by inject<BeamClient>()

    private val renderer by inject<IndexHtmlRenderer>()

    private val defaultFavicon = SpaceFavicon(
        appleTouch = Defaults.APPLE_TOUCH_ICON,
        favicon16 = Defaults.FAVICON_16,
        favicon32 = Defaults.FAVICON_32,
        faviconIco = Defaults.FAVICON_ICO
    )

    private val logger = logging()

    override suspend fun resolveSpace(request: SpaceRequest): ResolvedSpace {
        val spaceId = request.spaceIdentifier

        logger.d {
            "Resolving space $spaceId..."
        }

        spaceId ?: return resolveNotAvailableSpace()

        val languageCode = resolveLanguageCode()
        val spaceResult = client.getSpace(spaceId, languageCode)

        spaceResult.onFailure {
            logger.e(it) {
                "Failed to fetch space"
            }

            return resolveFailedSpace(err = it)
        }

        spaceResult.onSuccess {
            return resolveFoundSpace(
                space = it,
                languageCode = languageCode,
                request = request
            )
        }

        return resolveNotAvailableSpace()
    }

    private fun resolveFailedSpace(err: Throwable) =
        if (err is ClientRequestException && err.response.status == HttpStatusCode.NotFound) {
            logger.d {
                "Got 404"
            }

            resolveNotAvailableSpace()
        } else {
            logger.d {
                "Got something other than 404"
            }

            resolveNotAvailableSpace(message = Defaults.TITLE_NOT_AVAILABLE)
        }

    private fun resolveNotAvailableSpace(message: String = Defaults.TITLE_NOT_FOUND): ResolvedSpace {
        logger.d {
            "Resolving with unavailable space. Message: $message"
        }

        val parameters = RenderParameters(
            space = null,
            languageCode = null,
            title = message,
            description = null,
            icon = Defaults.ICON,
            favicon = defaultFavicon,
            urlPreview = null
        )

        val html = renderer.renderIndex(parameters)

        return ResolvedSpace(space = null, html)
    }

    private fun resolveFoundSpace(space: Space, languageCode: LanguageCode?, request: SpaceRequest): ResolvedSpace {
        logger.d {
            "Resolving with found space by identifier ${request.spaceIdentifier}..."
        }

        val view = space.view

        val title = view.title ?: Defaults.TITLE
        val description = view.description ?: Defaults.DESCRIPTION
        val icon = view.icon ?: Defaults.ICON

        val preview = view.preview

        val previewType = when (preview?.type) {
            SpaceUrlPreview.Type.DEFAULT -> UrlPreviewMetaTags.TYPE_SUMMARY
            SpaceUrlPreview.Type.LARGE -> UrlPreviewMetaTags.TYPE_SUMMARY_LARGE_IMAGE
            else -> Defaults.PREVIEW_TYPE
        }

        val urlPreview = UrlPreviewMetaTags(
            siteName = Defaults.SITE_NAME,
            title,
            description,
            image = preview?.image ?: icon,
            type = previewType
        )

        val parameters = RenderParameters(
            space,
            languageCode,
            title,
            description,
            icon,
            favicon = view.favicon ?: defaultFavicon,
            urlPreview
        )

        logger.d {
            "Render parameters: $parameters"
        }

        val html = renderer.renderIndex(parameters)

        return ResolvedSpace(space, html)
    }

    private suspend fun resolveLanguageCode() =
        client.getResolvedTranslation(Defaults.LANGUAGE_CODE).getOrNull()?.languageCode
}