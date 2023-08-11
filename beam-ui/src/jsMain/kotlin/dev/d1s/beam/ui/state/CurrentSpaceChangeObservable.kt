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
import dev.d1s.beam.ui.util.*
import io.kvision.state.ObservableValue
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class CurrentSpaceChange(
    val space: Space?,
    val type: Type? = null
) {
    enum class Type {
        ADDED, UPDATED, REMOVED
    }
}

class CurrentSpaceChangeObservable : Observable<CurrentSpaceChange>, KoinComponent {

    override val launchOnStartup = true

    override val state = ObservableValue(
        CurrentSpaceChange(currentSpace)
    )

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

            val change = CurrentSpaceChange(newSpace, CurrentSpaceChange.Type.UPDATED)
            state.setState(change)
        }
    }

    // Да что тебе вообще мешает? Что приносит в жизнь всё это дерьмо?
    // Это даже не выглядит нормально, как будто кто-то специально наносит вред
    // Если так, то вопросов много.
    // Меня это угнетает и убивает, я не хочу так, ты тоже.
    // Я не знаю, что мне делать.
    // Я чувствую гнев, боль, отчаяние.
    // У кого мне просить помощи? У Бога?
    // Я плохо справляюсь.
    // Я хочу, чтоб было хорошо.
    // Может быть, проблема в тебе? В твоем сознании?
    // ДА КАК ЖЕ СЛОЖНО!
    // Почему? Почему я не такой?
    // Люди разные. Не всем что-то дано.
    // Я срываюсь. Я не могу спокойно это воспринимать.
    // Я же буду жить с этим.
    // И все равно, я люблю её. Как бы то ни было.
    // Dear God... help your child.

    private suspend fun handleSpaceRemoval(spaceId: SpaceId) {
        client.onSpaceRemoved(id = spaceId) {
            setNullSpace(type = CurrentSpaceChange.Type.REMOVED)
        }
    }

    private fun setNullSpace(type: CurrentSpaceChange.Type?) {
        val change = CurrentSpaceChange(space = null, type)
        setCurrentSpace(space = null)
        state.setState(change)
    }

    private suspend fun handleSpaceCreation() {
        client.onSpaceCreated {
            val space = it.data
            val spaceId = space.id

            if (spaceId == currentSpaceIdentifier || space.slug == currentSpaceIdentifier) {
                setCurrentSpace(space)

                val change = CurrentSpaceChange(space, CurrentSpaceChange.Type.ADDED)
                state.setState(change)

                handleExistingSpaceUpdates(spaceId)
            }
        }
    }

    private fun handleDaemonStatus() {
        daemonStatusObservable.state.subscribeSkipping { status ->
            if (status == null) {
                setNullSpace(type = null)
            }
        }
    }
}