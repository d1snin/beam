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

package dev.d1s.beam.ui.state

import dev.d1s.beam.client.PublicBeamClient
import dev.d1s.beam.commons.Space
import dev.d1s.beam.commons.SpaceId
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.client.DaemonStatusWithPing
import dev.d1s.beam.ui.util.currentSpace
import dev.d1s.beam.ui.util.currentSpaceIdentifier
import dev.d1s.beam.ui.util.setCurrentSpace
import dev.d1s.beam.ui.util.setCurrentSpaceIdentifier
import io.kvision.state.ObservableValue
import io.kvision.state.sub
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CurrentSpaceChangeObservable : Observable<Space?>, KoinComponent {

    override val launchOnStartup = true

    override val state = ObservableValue(currentSpace)

    private val client by inject<PublicBeamClient>()

    private val daemonStatusObservable by inject<Observable<DaemonStatusWithPing?>>(Qualifier.DaemonStatusObservable)

    override fun monitor() =
        launchMonitor {
            currentSpace?.id?.let { spaceId ->
                handleExistingSpaceUpdates(spaceId)
            } ?: run {
                handleSpaceCreation()
            }

            handleDaemonStatus()
        }

    private suspend fun handleExistingSpaceUpdates(spaceId: SpaceId) {
        handleSpaceUpdate(spaceId)
        handleSpaceRemoval(spaceId)
    }

    private suspend fun handleSpaceUpdate(spaceId: SpaceId) {
        client.onSpaceUpdated(id = spaceId) {
            val (oldSpace, newSpace) = it.data
            val oldId = oldSpace.id
            val newId = newSpace.id
            val oldSlug = oldSpace.slug
            val newSlug = newSpace.slug

            if (
                (oldId != newId && currentSpaceIdentifier == oldId)
                || (oldSlug != newSlug && currentSpaceIdentifier == oldSlug)
            ) {
                setCurrentSpaceIdentifier(newSlug)
            }

            setCurrentSpace(newSpace)
            state.setState(newSpace)
        }
    }

    private suspend fun handleSpaceRemoval(spaceId: SpaceId) {
        client.onSpaceRemoved(id = spaceId) {
            setNullSpace()
        }
    }

    private fun setNullSpace() {
        setCurrentSpace(space = null)
        state.setState(null)
    }

    private suspend fun handleSpaceCreation() {
        client.onSpaceCreated {
            val space = it.data
            val spaceId = space.id

            if (spaceId == currentSpaceIdentifier || space.slug == currentSpaceIdentifier) {
                setCurrentSpace(space)
                state.setState(space)

                handleExistingSpaceUpdates(spaceId)
            }
        }
    }

    private fun handleDaemonStatus() {
        var skipped = false
        daemonStatusObservable.state.subscribe { status ->
            if (skipped) {
                if (status == null) {
                    setNullSpace()
                }
            }

            skipped = true
        }
    }
}