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

package dev.d1s.beam.bundle.html

import dev.d1s.beam.bundle.configuration.daemonHttpAddress
import dev.d1s.beam.bundle.configuration.daemonWsAddress
import dev.d1s.beam.bundle.entity.Html
import dev.d1s.beam.bundle.response.Defaults
import dev.d1s.beam.commons.*
import io.ktor.server.config.*
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class RenderParameters(
    val space: Space?,
    val title: String,
    val description: String?,
    val icon: SpaceIconUrl?,
    val favicon: SpaceFavicon,
    val themeColor: String?,
    val urlPreview: SpaceUrlPreview?
)

data class SpaceUrlPreview(
    val url: String,
    val siteName: String,
    val title: String,
    val description: String,
    val image: String
)

interface IndexHtmlRenderer {

    fun renderIndex(renderParameters: RenderParameters): Html
}

class DefaultIndexHtmlRenderer : IndexHtmlRenderer, KoinComponent {

    private val config by inject<ApplicationConfig>()

    override fun renderIndex(renderParameters: RenderParameters): Html =
        createHTML().html {
            lang = "en"

            head {
                title(renderParameters.title)
                meta("title", renderParameters.title)

                renderParameters.description?.let {
                    meta("description", it)
                }

                renderParameters.themeColor?.let {
                    meta("theme-color", it)
                    meta("msapplication-TileColor", it)
                }

                meta("charset", "utf-8")
                meta("viewport", "width=device-width, initial-scale=1")

                renderParameters.urlPreview?.let { preview ->
                    meta("og:type", "website")
                    meta("og:url", preview.url)
                    meta("og:site_name", preview.siteName)
                    meta("og:title", preview.title)
                    meta("og:description", preview.description)
                    meta("og:image", preview.image)

                    meta("twitter:card", "summary_large_image")
                    meta("twitter:url", preview.url)
                    meta("twitter:title", preview.title)
                    meta("twitter:description", preview.description)
                    meta("twitter:image", preview.image)
                }

                meta(DaemonConnectorMeta.HTTP, config.daemonHttpAddress)
                meta(DaemonConnectorMeta.WS, config.daemonWsAddress)

                renderParameters.space?.let {
                    meta(SpaceMeta.SPACE, it.id)
                }

                val favicon = renderParameters.favicon

                link(rel = "apple-touch-icon", href = favicon.appleTouch) {
                    sizes = "180x180"
                }

                link(rel = "icon", type = "image/png", href = favicon.favicon16) {
                    sizes = "16x16"
                }

                link(rel = "icon", type = "image/png", href = favicon.favicon32) {
                    sizes = "32x32"
                }

                link(rel = "mask-icon", href = favicon.maskIcon) {
                    attributes["color"] = favicon.maskIconColor ?: ""
                }

                dependencies.forEach { dependency ->
                    dependency()
                }

                script {
                    src = Defaults.SCRIPT
                }
            }

            body {
                div {
                    id = "root"
                }
            }
        }
}