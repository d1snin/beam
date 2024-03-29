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

package dev.d1s.beam.ui.component

import dev.d1s.beam.ui.theme.currentTheme
import dev.d1s.beam.ui.theme.setBackground
import dev.d1s.beam.ui.theme.setOutline
import dev.d1s.beam.ui.theme.setOverlay
import dev.d1s.exkt.kvision.bootstrap.rounded
import dev.d1s.exkt.kvision.bootstrap.shadow
import io.kvision.html.div
import io.kvision.panel.SimplePanel

fun SimplePanel.renderStyledCard(
    usePageBackground: Boolean = false,
    bare: Boolean = false,
    block: (SimplePanel.() -> Unit)? = null
): SimplePanel =
    div {
        if (!bare) {
            rounded()

            if (currentTheme.shadow) {
                shadow()
            }

            setOutline()
            setBackground(usePageBackground)
        }

        block?.invoke(this)
    }

private fun SimplePanel.setBackground(usePageBackground: Boolean) {
    if (usePageBackground) {
        setBackground()
    } else {
        setOverlay()
    }
}