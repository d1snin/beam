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

import dev.d1s.beam.client.PublicBeamClient
import dev.d1s.beam.commons.DaemonStatus
import dev.d1s.beam.ui.theme.currentTheme
import dev.d1s.beam.ui.util.Texts
import dev.d1s.beam.ui.util.iconWithMargin
import dev.d1s.exkt.kvision.component.Component
import io.kvision.core.Background
import io.kvision.core.Outline
import io.kvision.core.OutlineStyle
import io.kvision.html.div
import io.kvision.html.span
import io.kvision.panel.SimplePanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.utils.px
import io.kvision.utils.rem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.time.measureDuration
import kotlin.time.Duration.Companion.seconds

class DaemonStatusComponent : Component<Unit>(), KoinComponent {

    private val client by inject<PublicBeamClient>()

    private val daemonStatus = ObservableValue<DaemonStatusWithPing?>(null)
    private val monitoringScope = CoroutineScope(Dispatchers.Main)

    override fun SimplePanel.render() {
        div(className = "rounded shadow px-2 text-white d-flex align-items-center").bind(daemonStatus) { status ->
            visible = false

            if (status != null) {
                visible = true

                height = 40.px

                background = Background(color = currentTheme.overlay)
                outline = Outline(width = 1.px, style = OutlineStyle.SOLID, color = currentTheme.outline)

                div {
                    fontSize = 1.4.rem
                    color = currentTheme.green
                    iconWithMargin("bi bi-cloud")
                }

                span(Texts.Heading.DaemonStatus.CONNECTED, className = "me-2")
                span {
                    color = currentTheme.green
                    +(status.ping.toString() + Texts.Heading.DaemonStatus.MS_UNIT)
                }
            }
        }

        monitorDaemonStatus()
    }

    private fun monitorDaemonStatus() {
        monitoringScope.launch {
            while (true) {
                var status: DaemonStatus? = null

                val ping = measureDuration {
                    status = client.getDaemonStatus().getOrNull()
                }

                status ?: run {
                    daemonStatus.value = null
                }

                status?.let {
                    daemonStatus.value = DaemonStatusWithPing(it, ping.toInt())
                }

                delay(3.seconds)
            }
        }
    }
}

private data class DaemonStatusWithPing(
    val status: DaemonStatus,
    val ping: Int
)