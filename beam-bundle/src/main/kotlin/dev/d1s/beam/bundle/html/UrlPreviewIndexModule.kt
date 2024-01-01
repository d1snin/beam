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

package dev.d1s.beam.bundle.html

import kotlinx.html.HEAD
import kotlinx.html.meta
import org.koin.core.component.KoinComponent

class UrlPreviewIndexModule : IndexModule, KoinComponent {

    override fun HEAD.render(renderParameters: RenderParameters) {
        renderParameters.urlPreview?.let { preview ->
            meta("og:type", "website")
            meta("og:site_name", preview.siteName)
            meta("og:title", preview.title)
            meta("og:description", preview.description)
            meta("og:image", preview.image)

            meta("twitter:card", preview.type)
            meta("twitter:title", preview.title)
            meta("twitter:description", preview.description)
            meta("twitter:image", preview.image)
        }
    }
}