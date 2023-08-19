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

package dev.d1s.beam.ui.component

import dev.d1s.beam.ui.state.bindToCurrentTheme
import dev.d1s.beam.ui.theme.currentTheme
import io.kvision.core.*
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import io.kvision.utils.px

fun SimplePanel.card(
    className: String? = null,
    backgroundColor: (() -> Color)? = null,
    block: (SimplePanel.() -> Unit)? = null
): SimplePanel {
    val classes = "rounded shadow" + (className?.let { " $it" } ?: "")

    return div(className = classes) {
        bindToCurrentTheme {
            background = Background(color = backgroundColor?.invoke() ?: currentTheme.overlay)
            setOutline(currentTheme.outline)
        }

        block?.invoke(this)
    }
}

fun StyledComponent.setOutline(color: Color) {
    outline = Outline(width = 1.px, style = OutlineStyle.SOLID, color)
}