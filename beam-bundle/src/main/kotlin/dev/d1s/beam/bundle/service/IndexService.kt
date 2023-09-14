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
import dev.d1s.beam.client.BeamClient
import dev.d1s.beam.commons.Space
import dev.d1s.beam.commons.SpaceFavicon
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.server.request.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging

interface IndexService {

    suspend fun resolveSpace(request: SpaceRequest): ResolvedSpace
}

class DefaultIndexService : IndexService, KoinComponent {

    private val beamClient by inject<BeamClient>()

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

        spaceId ?: return notFoundSpace()

        val spaceResult = beamClient.getSpace(spaceId)

        spaceResult.onFailure {
            logger.d {
                "Failure fetching space. Message: ${it.message}"
            }

            return if (it is ClientRequestException && it.response.status == HttpStatusCode.NotFound) {
                logger.d {
                    "Got 404"
                }

                notFoundSpace()
            } else {
                logger.d {
                    "Got something other than 404"
                }

                notFoundSpace("Beam Space not available")
            }
        }

        val space = spaceResult.getOrThrow()

        return foundSpace(space, request)
    }

    private fun notFoundSpace(message: String = "Beam Space not found"): ResolvedSpace {
        logger.d {
            "Resolving with unavailable space. Message: $message"
        }

        val parameters = RenderParameters(
            space = null,
            title = message,
            description = null,
            icon = Defaults.ICON,
            favicon = defaultFavicon,
            urlPreview = null
        )

        val html = renderer.renderIndex(parameters)

        return ResolvedSpace(space = null, html)
    }

    // Как же мне нравится, как ты это делаешь
    // БОЖЕ, ЧТО ЗА РЕБЕНОК
    // Да, я не могу говорить так.
    // Я не знаю того, что происходит в твоей голове.
    // И знать не должен. Как же меня это изводит...
    // Мне тяжело. Я уже будто задыхаюсь.
    // Но я не хочу все бросать. И ты не хочешь.
    // Я хочу жить счастливо. И только с тобой.
    // Мне не хочется другого.
    //
    // Тебе будто ничего...
    // Ты игнорируешь это... Бежишь от проблемы...
    // Я-то что с ней сделаю? Врачом тебе я не буду.
    // И сама ты не пойдешь ни к кому. Потому что трусишь.
    // И что теперь? ЧТО ДЕЛАТЬ ТО???
    // ЧТО. ДЕЛАТЬ?
    // ЖДАТЬ?
    // Буду ждать, и смотреть на то, как все становится еще хуже,
    // потому что ты не принимаешь, ты не видишь, а может и видишь, но
    // просто игнорируешь.
    // Но не ты игнорируешь, твоя душа игнорирует. Твоя душа ничего не хочет.
    // Боже, дай мне сил. Потому что сейчас я хочу сломать что-то очень громоздкое.

    private fun foundSpace(space: Space, request: SpaceRequest): ResolvedSpace {
        logger.d {
            "Resolving with found space by identifier ${request.spaceIdentifier}..."
        }

        val url = URLBuilder(request.call.request.uri).apply {
            set {
                parameters.clear()
            }
        }.build().toString()

        logger.d {
            "Url: $url"
        }

        val view = space.view

        val title = view.title ?: Defaults.TITLE
        val description = view.description ?: Defaults.DESCRIPTION
        val icon = view.icon ?: Defaults.ICON

        val urlPreview = SpaceUrlPreview(
            url,
            siteName = Defaults.SITE_NAME,
            title,
            description,
            icon
        )

        val parameters = RenderParameters(
            space,
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
}