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

import dev.d1s.beam.commons.Space
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.client.DaemonStatusWithPing
import dev.d1s.beam.ui.state.Observable
import dev.d1s.beam.ui.util.currentBlocks
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.render
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import io.kvision.state.bind
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SpaceContentComponent : Component<Unit>(), KoinComponent {

    private val daemonStatusObservable by inject<Observable<DaemonStatusWithPing?>>(Qualifier.DaemonStatusObservable)

    private val currentSpaceChangeObservable by inject<Observable<Space?>>(Qualifier.CurrentSpaceChangeObservable)

    private val disconnectedDaemonStatusBlankslate by inject<Component<Unit>>(Qualifier.DisconnectedDaemonStatusBlankslateComponent)

    private val spaceSearchCardComponent by inject<Component<SpaceSearchCardComponent.Config>>(Qualifier.SpaceSearchCardComponent)

    override fun SimplePanel.render() {
        div(className = "container-fluid mt-4").bind(
            daemonStatusObservable.state,
            runImmediately = false
        ) { status ->
            if (status != null) {
                handleNotFound()
                handleEmptySpace()
            } else {
                render(disconnectedDaemonStatusBlankslate)
            }
        }
    }

    private fun SimplePanel.handleNotFound() {
        bind(currentSpaceChangeObservable.state) { space ->
            if (space == null) {
                render(spaceSearchCardComponent) {
                    mode.value = SpaceSearchCardComponent.Mode.NOT_FOUND
                }
            }
        }
    }

    private fun SimplePanel.handleEmptySpace() {
        if (currentBlocks?.isEmpty() == true) {
            render(spaceSearchCardComponent) {
                mode.value = SpaceSearchCardComponent.Mode.EMPTY_SPACE
            }
        }
    }
}