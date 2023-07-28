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
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.client.DaemonConnector
import dev.d1s.beam.ui.client.down
import dev.d1s.beam.ui.util.currentSpace
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

    private val client by inject<PublicBeamClient>()

    private val disconnectedDaemonStatusBlankslate by inject<Component<Unit>>(Qualifier.DisconnectedDaemonStatusBlankslateComponent)

    private val spaceSearchCardComponent by inject<Component<SpaceSearchCardComponent.Config>>(Qualifier.SpaceSearchCardComponent)

    private val renderingScope = CoroutineScope(Dispatchers.Main)

    override fun SimplePanel.render() {
        div(className = "container-fluid mt-4").bind(
            daemonConnector.observableStatus,
            runImmediately = false
        ) { status ->
            renderingScope.launch {
                if (status.down()) {
                    render(disconnectedDaemonStatusBlankslate)
                } else {
                    handleNotFound()
                    handleEmptySpace()
                }
            }
        }
    }

    private suspend fun SimplePanel.handleNotFound() {
        if (currentSpace() == null && daemonConnector.isUp() == true) {
            render(spaceSearchCardComponent) {
                mode.value = SpaceSearchCardComponent.Mode.NOT_FOUND
            }
        }
    }

    private suspend fun SimplePanel.handleEmptySpace() {
        currentSpace()?.let {
            val blocks = client.getBlocks(it.id).getOrNull()

            if (blocks?.isEmpty() == true) {
                render(spaceSearchCardComponent) {
                    mode.value = SpaceSearchCardComponent.Mode.EMPTY_SPACE
                }
            }
        }
    }
}