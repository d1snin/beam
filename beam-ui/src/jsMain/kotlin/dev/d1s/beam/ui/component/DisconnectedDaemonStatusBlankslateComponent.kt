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

import dev.d1s.beam.ui.theme.setSecondaryText
import dev.d1s.beam.ui.util.currentTranslation
import dev.d1s.beam.ui.util.disconnectedDaemonStatusReporterFirstLine
import dev.d1s.beam.ui.util.disconnectedDaemonStatusReporterSecondLine
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import io.kvision.core.AlignItems
import io.kvision.html.div
import io.kvision.html.p
import io.kvision.panel.SimplePanel
import io.kvision.panel.vPanel
import io.kvision.utils.vh
import org.koin.core.component.KoinComponent

class DisconnectedDaemonStatusBlankslateComponent : Component<Unit>(), KoinComponent {

    override fun SimplePanel.render(): Effect {
        div(className = "container-fluid d-flex justify-content-center") {
            marginTop = 20.vh

            setSecondaryText()

            renderTextPanel()
        }

        return Effect.Success
    }

    private fun SimplePanel.renderTextPanel() {
        vPanel(alignItems = AlignItems.CENTER) {
            h2(currentTranslation.disconnectedDaemonStatusReporterFirstLine)
            h2(currentTranslation.disconnectedDaemonStatusReporterSecondLine)
        }
    }

    private fun SimplePanel.h2(text: String) {
        p(className = "h3 text-center") {
            +text
        }
    }
}