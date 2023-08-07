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
import dev.d1s.beam.ui.state.CurrentSpaceChange
import dev.d1s.beam.ui.state.CurrentSpaceContentChange
import dev.d1s.beam.ui.state.Observable
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.render
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SpaceContentComponent : Component<Unit>(), KoinComponent {

    private val daemonStatusObservable by inject<Observable<DaemonStatusWithPing?>>(Qualifier.DaemonStatusObservable)

    private val currentSpaceChangeObservable by inject<Observable<CurrentSpaceChange>>(Qualifier.CurrentSpaceChangeObservable)

    private val currentSpaceContentChangeObservable by inject<Observable<CurrentSpaceContentChange>>(Qualifier.CurrentSpaceContentChangeObservable)

    private val blockContainerComponent by inject<Component<Unit>>(Qualifier.BlockContainerComponent)

    private val disconnectedDaemonStatusBlankslateComponent by inject<Component<Unit>>(Qualifier.DisconnectedDaemonStatusBlankslateComponent)

    private val spaceSearchCardComponent by inject<Component<SpaceSearchCardComponent.Config>>(Qualifier.SpaceSearchCardComponent)

    private val showBlockContainer = ObservableValue(true)

    override fun SimplePanel.render() {
        div(className = "container-fluid mt-4") {
            bind(daemonStatusObservable.state, runImmediately = false) { status ->
                if (status != null) {
                    handleNotFound()
                    handleEmptySpace()
                    spaceContent()
                } else {
                    render(disconnectedDaemonStatusBlankslateComponent)
                }
            }
        }
    }

    private fun SimplePanel.handleNotFound() {
        div().bind(currentSpaceChangeObservable.state) {
            val space = it.space

            if (space == null) {
                showBlockContainer.setState(false)

                render(spaceSearchCardComponent) {
                    mode.value = SpaceSearchCardComponent.Mode.NOT_FOUND
                }
            }
        }
    }

    private fun SimplePanel.spaceContent() {
        div().bind(showBlockContainer) { show ->
            if (show) {
                render(blockContainerComponent)
            }
        }
    }

    private fun SimplePanel.handleEmptySpace() {
        div().bind(currentSpaceContentChangeObservable.state) {
            val blocks = it.blocks

            if (blocks?.isEmpty() == true) {
                showBlockContainer.setState(false)

                render(spaceSearchCardComponent) {
                    mode.value = SpaceSearchCardComponent.Mode.EMPTY_SPACE
                }
            }
        }
    }
}