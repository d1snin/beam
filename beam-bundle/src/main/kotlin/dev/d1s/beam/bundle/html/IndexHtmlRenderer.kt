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

import dev.d1s.beam.bundle.entity.Html

data class RenderParameters(
    val title: String,
    val description: String?,
    val themeColor: String?,
    val urlPreview: SpaceUrlPreview?
)

data class SpaceUrlPreview(
    val url: String?,
    val siteName: String?,
    val title: String?,
    val description: String?,
    val image: String?
)

interface IndexHtmlRenderer {

    fun renderIndex(renderParameters: RenderParameters): Html
}

class DefaultIndexHtmlRenderer : IndexHtmlRenderer {

    override fun renderIndex(renderParameters: RenderParameters): Html {
        TODO("Not yet implemented")
    }
}