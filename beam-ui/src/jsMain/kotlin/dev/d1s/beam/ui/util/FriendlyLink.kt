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

package dev.d1s.beam.ui.util

import dev.d1s.beam.ui.theme.setTextColor
import dev.d1s.exkt.kvision.bootstrap.textDecorationNone
import io.kvision.html.Link
import io.kvision.html.link
import io.kvision.panel.SimplePanel

fun SimplePanel.renderFriendlyLink(
    label: String = "",
    url: String? = null,
    icon: String? = null,
    className: String? = null,
    external: Boolean = false,
    download: Boolean = false,
    downloadName: String? = null,
    init: Link.() -> Unit = {}
) =
    link(label, url, icon = icon, className = className) {
        if (external) {
            setAttribute("target", "_blank")
            setAttribute("rel", "noopener noreferrer")
        }

        if (download) {
            setAttribute("download", downloadName ?: "")
        }

        init()
    }

fun SimplePanel.renderUnstyledLink(
    url: String,
    external: Boolean = false,
    download: Boolean = false,
    downloadName: String? = null,
    block: SimplePanel.() -> Unit
) {
    renderFriendlyLink(
        url = url,
        external = external,
        download = download,
        downloadName = downloadName
    ) {
        textDecorationNone()

        setTextColor()
        block()
    }
}