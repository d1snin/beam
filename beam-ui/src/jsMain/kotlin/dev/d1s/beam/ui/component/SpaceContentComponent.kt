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
import dev.d1s.beam.ui.client.DaemonConnector
import dev.d1s.beam.ui.client.down
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.render
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import io.kvision.state.bind
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SpaceContentComponent : Component<Unit>(), KoinComponent {

    private val daemonConnector by inject<DaemonConnector>()

    private val disconnectedDaemonStatusBlankslate by inject<Component<Unit>>(Qualifier.DisconnectedDaemonStatusBlankslateComponent)

    private val renderingScope = CoroutineScope(Dispatchers.Main)

    override fun SimplePanel.render() {
        div(className = "container-fluid").bind(daemonConnector.observableStatus) { status ->
            renderingScope.launch {
                if (status.down()) {
                    render(disconnectedDaemonStatusBlankslate)
                }
            }
        }
    }
}