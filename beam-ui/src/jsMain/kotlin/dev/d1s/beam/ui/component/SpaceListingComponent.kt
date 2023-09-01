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
import dev.d1s.beam.client.response.Spaces
import dev.d1s.beam.commons.Role
import dev.d1s.beam.commons.Space
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.theme.setSecondaryBlue
import dev.d1s.beam.ui.theme.setSecondaryText
import dev.d1s.beam.ui.util.currentSpace
import dev.d1s.beam.ui.util.currentTranslation
import dev.d1s.beam.ui.util.spaceListingFetchMoreButton
import dev.d1s.beam.ui.util.spaceListingMessage
import dev.d1s.exkt.common.pagination.Paginator
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import dev.d1s.exkt.kvision.component.MutableLazyEffectState
import dev.d1s.exkt.kvision.component.render
import io.kvision.core.onClick
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.html.p
import io.kvision.panel.SimplePanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.utils.rem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

private typealias TotalElements = Int
private typealias FetchedSpaces = Pair<List<Space>, TotalElements>

class SpaceListingComponent : Component<Unit>(), KoinComponent {

    private val beamClient by inject<PublicBeamClient>()

    private val spaces = ObservableValue(listOf<Space>() to 0)

    private val paginator = Paginator(PAGE_LIMIT, currentPage = 0)

    private val renderingScope = CoroutineScope(Dispatchers.Main)

    override fun SimplePanel.render(): Effect {
        val (state, effect) = Effect.lazy()

        renderContainerAndLaunch {
            getMoreSpaces()
            renderSpaceContainer(state)
        }

        return effect
    }

    private fun SimplePanel.renderContainerAndLaunch(block: suspend SimplePanel.() -> Unit) {
        div {
            renderingScope.launch {
                block()
            }
        }
    }

    private fun SimplePanel.renderSpaceContainer(state: MutableLazyEffectState) {
        renderBoundContainer(state) { (fetchedSpaces, totalCount) ->
            renderSpaceListingMessage()

            renderSpaceRow(fetchedSpaces) {
                renderRow(fetchedSpaces)
            }

            if (totalCount !in (paginator.offset + 1)..paginator.limit) {
                renderFetchMoreButton()
            }
        }
    }

    private fun SimplePanel.renderBoundContainer(
        effectState: MutableLazyEffectState,
        block: SimplePanel.(FetchedSpaces) -> Unit
    ) {
        div().bind(spaces) { fetchedSpaces ->
            val (spaces, _) = fetchedSpaces
            val isNotEmpty = spaces.isNotEmpty()

            if (isNotEmpty) {
                block(fetchedSpaces)
            }

            effectState.value = isNotEmpty
        }
    }

    private fun SimplePanel.renderSpaceListingMessage() {
        p(currentTranslation.spaceListingMessage, className = "fs-bold") {
            fontSize = 1.1.rem

            setSecondaryText()
        }
    }

    private fun SimplePanel.renderRow(spaces: List<Space>) {
        spaces.forEach { space ->
            renderCol {
                val spaceCard = get<Component<SpaceCardComponent.Config>>(Qualifier.SpaceCardComponent)

                render(spaceCard) {
                    this.space.value = space
                }
            }
        }
    }

    private fun SimplePanel.renderSpaceRow(spaces: List<Space>, block: SimplePanel.() -> Unit) {
        val lgCols = if (spaces.size == 1) 1 else 2

        div(className = "row row-cols-1 row-cols-lg-$lgCols g-3") {
            block()
        }
    }

    private fun SimplePanel.renderCol(block: SimplePanel.() -> Unit) {
        div(className = "col") {
            block()
        }
    }

    private suspend fun getMoreSpaces() {
        paginator.currentPage++
        getSpaces()?.let { fetchedSpaces ->
            spaces.value = FetchedSpaces(spaces.value.first + fetchedSpaces.first, fetchedSpaces.second)
        }
    }

    private suspend fun getSpaces(offset: Int = paginator.offset): FetchedSpaces? =
        beamClient.getSpaces(paginator.limit, offset).getOrNull()?.let { page ->
            val spaces = page.elements
            val filteredSpaces = spaces.filterSpaces()
            val totalCount = page.count()

            filteredSpaces to totalCount
        }

    private fun List<Space>.filterSpaces() =
        toMutableList().apply {
            val rootSpace = getRoot()

            rootSpace?.let {
                remove(it)
                add(0, it)
            }
        }.filter {
            it.id != currentSpace?.id
        }

    private fun List<Space>.getRoot() =
        find {
            it.role == Role.ROOT
        }

    private fun Spaces.count() =
        totalCount.let {
            if (currentSpace != null) {
                it - 1
            } else {
                it
            }
        }

    private fun SimplePanel.renderFetchMoreButton() {
        div(className = "d-flex w-100 justify-content-center mt-2") {
            button(currentTranslation.spaceListingFetchMoreButton, style = ButtonStyle.LINK) {
                setSecondaryBlue()
                handleButtonClick()
            }
        }
    }

    private fun SimplePanel.handleButtonClick() {
        onClick {
            renderingScope.launch {
                getMoreSpaces()
            }
        }
    }

    private companion object {

        private const val PAGE_LIMIT = 4
    }
}