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

import dev.d1s.beam.commons.BlockSize
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.client.DaemonStatusWithPing
import dev.d1s.beam.ui.state.Observable
import dev.d1s.beam.ui.state.SpaceContentChange
import dev.d1s.beam.ui.util.currentSpace
import dev.d1s.beam.ui.util.subscribeSkipping
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import dev.d1s.exkt.kvision.component.render
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private data class ShowBlockContainer(
    val notFound: Boolean = false,
    val emptySpace: Boolean = false,
    val lostConnection: Boolean = false
) {
    val allPassing = !notFound && !emptySpace && !lostConnection
}

class SpaceContentComponent : Component<Unit>(), KoinComponent {

    private val currentSpaceContentChangeObservable by inject<Observable<SpaceContentChange?>>(Qualifier.CurrentSpaceContentChangeObservable)

    private val maxBlockSizeChangeObservable by inject<Observable<BlockSize>>(Qualifier.MaxBlockSizeChangeObservable)

    private val daemonStatusWithPingObservable by inject<Observable<DaemonStatusWithPing?>>(Qualifier.DaemonStatusWithPingObservable)

    private val blockContainerComponent by inject<Component<Unit>>(Qualifier.BlockContainerComponent)

    private val failureCardComponent by inject<Component<FailureCardComponent.Config>>(Qualifier.FailureCardComponent)

    private val showBlockContainerState = ObservableValue(ShowBlockContainer())

    override fun SimplePanel.render(): Effect {
        div(className = "container-fluid") {
            div().bind(maxBlockSizeChangeObservable.state) {
                render(failureCardComponent)

                handleNotFound()
                handleEmptySpace()
                handleLostDaemonConnection()
                renderSpaceContent()
            }
        }

        return Effect.Success
    }

    private fun handleNotFound() {
        val notFound = currentSpace == null
        setStateSafe(notFound = notFound)
    }

    private fun handleEmptySpace() {
        currentSpaceContentChangeObservable.state.subscribe { change ->
            val emptySpace = change?.blocks?.isEmpty() == true
            setStateSafe(emptySpace = emptySpace)
        }
    }

    private fun handleLostDaemonConnection() {
        daemonStatusWithPingObservable.state.subscribeSkipping { status ->
            val lostConnection = status == null
            setStateSafe(lostConnection = lostConnection)
        }
    }

    private fun SimplePanel.renderSpaceContent() {
        div().bind(showBlockContainerState) { show ->
            if (show.allPassing) {
                render(blockContainerComponent)
            }
        }
    }

    private fun setStateSafe(
        notFound: Boolean = showBlockContainerState.value.notFound,
        emptySpace: Boolean = showBlockContainerState.value.emptySpace,
        lostConnection: Boolean = showBlockContainerState.value.lostConnection
    ) {
        val show = ShowBlockContainer(notFound, emptySpace, lostConnection)

        failureCardComponent.apply {
            mode.value = when {
                // priority matters
                show.lostConnection -> FailureCardComponent.Mode.LOST_CONNECTION
                show.notFound -> FailureCardComponent.Mode.NOT_FOUND
                show.emptySpace -> FailureCardComponent.Mode.EMPTY_SPACE

                else -> FailureCardComponent.Mode.NONE
            }
        }

        if (showBlockContainerState.value != show) {
            showBlockContainerState.value = show
        }
    }
}