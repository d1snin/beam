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

package dev.d1s.beam.bundle.response

import dev.d1s.beam.bundle.html.UrlPreviewMetaTags

object Defaults {

    const val TITLE = "Beam Space"
    const val DESCRIPTION = "Explore this public space created on Beam."

    const val SITE_NAME = "Beam"

    const val PREVIEW_TYPE = UrlPreviewMetaTags.TYPE_SUMMARY

    const val ICON = "/icon.png"

    const val APPLE_TOUCH_ICON = "/apple-touch-icon.png"
    const val FAVICON_16 = "/favicon-16x16.png"
    const val FAVICON_32 = "/favicon-32x32.png"
    const val FAVICON_ICO = "/favicon.ico"

    const val SCRIPT = "/main.bundle.js"
}
