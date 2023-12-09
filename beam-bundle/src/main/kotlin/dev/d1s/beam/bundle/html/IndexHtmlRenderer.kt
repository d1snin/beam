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

import dev.d1s.beam.bundle.response.Defaults
import dev.d1s.beam.commons.Html
import dev.d1s.beam.commons.Space
import dev.d1s.beam.commons.SpaceFavicon
import dev.d1s.beam.commons.SpaceIconUrl
import dev.d1s.exkt.common.withEach
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext

data class RenderParameters(
    val space: Space?,
    val title: String,
    val description: String?,
    val icon: SpaceIconUrl?,
    val favicon: SpaceFavicon,
    val urlPreview: UrlPreviewMetaTags?
)

data class UrlPreviewMetaTags(
    val siteName: String,
    val title: String,
    val description: String,
    val image: String,
    val type: String
) {
    companion object {

        const val TYPE_SUMMARY = "summary"
        const val TYPE_SUMMARY_LARGE_IMAGE = "summary_large_image"
    }
}

interface IndexHtmlRenderer {

    fun renderIndex(renderParameters: RenderParameters): Html
}

class DefaultIndexHtmlRenderer : IndexHtmlRenderer, KoinComponent {

    private val indexModules by lazy {
        GlobalContext.get().getAll<IndexModule>()
    }

    override fun renderIndex(renderParameters: RenderParameters): Html =
        createHTML().html {
            lang = "en"

            indexModules.withEach {
                render(renderParameters)
            }

            renderHead(renderParameters)
            renderBody(renderParameters)
        }

    private fun HTML.renderHead(renderParameters: RenderParameters) {
        head {
            indexModules.withEach {
                render(renderParameters)
            }

            Dependencies.forEach { dependency ->
                dependency()
            }

            script {
                src = Defaults.SCRIPT
            }
        }
    }

    private fun HTML.renderBody(renderParameters: RenderParameters) {
        body {
            div {
                id = "root"
            }

            indexModules.withEach {
                render(renderParameters)
            }
        }
    }
}