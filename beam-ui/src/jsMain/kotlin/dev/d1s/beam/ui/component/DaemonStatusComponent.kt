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

import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.client.DaemonStatusWithPing
import dev.d1s.beam.ui.state.Observable
import dev.d1s.beam.ui.theme.currentTheme
import dev.d1s.beam.ui.util.*
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import io.kvision.core.Color
import io.kvision.html.div
import io.kvision.html.span
import io.kvision.panel.SimplePanel
import io.kvision.state.bind
import io.kvision.utils.px
import io.kvision.utils.rem
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DaemonStatusComponent : Component<Unit>(), KoinComponent {

    private val daemonStatusWithPingObservable by inject<Observable<DaemonStatusWithPing?>>(Qualifier.DaemonStatusWithPingObservable)

    override fun SimplePanel.render(): Effect {
        renderContainer { status ->
            renderStatusCard(status)
        }

        return Effect.Success
    }

    private fun SimplePanel.renderContainer(block: SimplePanel.(DaemonStatusWithPing?) -> Unit) {
        div().bind(daemonStatusWithPingObservable.state, runImmediately = false) { status ->
            block(status)
        }
    }

    private fun SimplePanel.renderStatusCard(status: DaemonStatusWithPing?) {
        renderCard(className = "d-flex align-items-center px-2") {
            if (status != null) {
                renderConnectedState(status)
            } else {
                renderDisconnectedState()
            }
        }
    }

    private fun SimplePanel.renderConnectedState(status: DaemonStatusWithPing) {
        applyStyle()

        renderCloudIcon(currentTheme.green)
        renderText(currentTranslation.daemonStatusConnected)

        renderPing(status)
    }

    private fun SimplePanel.renderPing(status: DaemonStatusWithPing) {
        span {
            val ping = status.ping

            color = when {
                ping < 150 -> currentTheme.green
                ping in 150..<250 -> currentTheme.orange
                else -> currentTheme.red
            }

            +(ping.toString() + currentTranslation.daemonStatusMsUnit)
        }
    }

    private fun SimplePanel.renderDisconnectedState() {
        applyStyle()

        renderCloudIcon(currentTheme.red)
        renderText(currentTranslation.daemonStatusDisconnected)
    }

    private fun SimplePanel.applyStyle() {
        height = 30.px
        fontSize = 0.8.rem
    }

    private fun SimplePanel.renderCloudIcon(iconColor: Color) {
        div {
            fontSize = 1.rem
            color = iconColor
            iconWithMargin("bi bi-cloud")
        }
    }

    private fun SimplePanel.renderText(text: String) {
        span(text, className = "me-2")
    }
}