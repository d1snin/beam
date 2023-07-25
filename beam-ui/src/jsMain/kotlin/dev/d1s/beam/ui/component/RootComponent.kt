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

import dev.d1s.beam.ui.theme.currentTheme
import dev.d1s.exkt.kvision.component.Component
import io.kvision.panel.SimplePanel
import io.kvision.utils.vh
import kotlinx.browser.document
import org.koin.core.component.KoinComponent

class RootComponent : Component.Root(), KoinComponent {

    override fun SimplePanel.render() {
        sizing()
        display()
        background()
    }

    private fun SimplePanel.sizing() {
        minHeight = 100.vh
    }

    private fun SimplePanel.display() {
        addCssClass("d-flex")
        addCssClass("flex-column")
    }

    private fun background() {
        requireNotNull(document.body).style.apply {
            backgroundColor = currentTheme.background.asString()
            display = "flex"
            flexDirection = "column"
            minHeight = "100vh"
            height = "100vh"
        }
    }
}