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

import dev.d1s.beam.ui.client.DaemonConnector
import dev.d1s.beam.ui.client.down
import dev.d1s.beam.ui.theme.currentTheme
import dev.d1s.beam.ui.util.Texts
import dev.d1s.exkt.kvision.component.Component
import io.kvision.core.AlignItems
import io.kvision.html.div
import io.kvision.html.p
import io.kvision.panel.SimplePanel
import io.kvision.panel.vPanel
import io.kvision.state.bind
import io.kvision.utils.vh
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DisconnectedDaemonStatusReporter : Component<Unit>(), KoinComponent {

    private val daemonConnector by inject<DaemonConnector>()

    private val renderingScope = CoroutineScope(Dispatchers.Main)

    override fun SimplePanel.render() {
        div(className = "container-fluid d-flex justify-content-center").bind(daemonConnector.observableStatus) { status ->
            visible = false

            renderingScope.launch {
                if (status.down()) {
                    marginTop = 20.vh
                    color = currentTheme.secondaryText

                    vPanel(alignItems = AlignItems.CENTER) {
                        h2(Texts.Body.DisconnectedDaemonStatusReporter.ALERT_FIRST_LINE)
                        h2(Texts.Body.DisconnectedDaemonStatusReporter.ALERT_SECOND_LINE)
                    }

                    visible = true
                } else {
                    visible = false
                }
            }
        }
    }

    private fun SimplePanel.h2(text: String) {
        p(className = "h3 text-center") {
            +text
        }
    }
}