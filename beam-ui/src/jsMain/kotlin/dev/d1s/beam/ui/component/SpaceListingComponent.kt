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
import dev.d1s.beam.commons.Role
import dev.d1s.beam.commons.Space
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.state.SpaceFetchingDelay
import dev.d1s.beam.ui.state.bindToCurrentTheme
import dev.d1s.beam.ui.state.launchMonitor
import dev.d1s.beam.ui.theme.currentTheme
import dev.d1s.beam.ui.util.Texts
import dev.d1s.beam.ui.util.currentSpace
import dev.d1s.exkt.common.pagination.Paginator
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.render
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.html.p
import io.kvision.panel.SimplePanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.utils.rem
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

private typealias TotalElements = Int
private typealias FetchedSpaces = Pair<List<Space>, TotalElements>

class SpaceListingComponent : Component<Unit>(), KoinComponent {

    private val beamClient by inject<PublicBeamClient>()

    private val spaces = ObservableValue(listOf<Space>() to 0)

    private val paginator = Paginator(PAGE_LIMIT, currentPage = 1)

    private val renderingScope = CoroutineScope(Dispatchers.Main)

    private val fetchingSpaces = atomic(false)

    override fun SimplePanel.render() {
        fetchAllSpaces()

        div().bind(spaces, runImmediately = false) { (fetchedSpaces, totalCount) ->
            println("spaceListing render: fetchedSpaces: $fetchedSpaces")

            if (fetchedSpaces.isNotEmpty()) {
                p(Texts.SpaceListing.CALLOUT, className = "fs-bold") {
                    fontSize = 1.1.rem

                    bindToCurrentTheme {
                        color = currentTheme.secondaryText
                    }
                }

                spaceRow(fetchedSpaces) {
                    renderRow(fetchedSpaces)
                }

                println("spaceListing render: totalCount: $totalCount, paginator.offset: ${paginator.offset}, paginator.limit: ${paginator.limit}")

                if (totalCount !in (paginator.offset + 1)..paginator.limit) {
                    fetchMoreButton()
                }
            }
        }
    }

    private fun SimplePanel.renderRow(spaces: List<Space>) {
        spaces.forEach { space ->
            col {
                val spaceCard = get<Component<SpaceCardComponent.Config>>(Qualifier.SpaceCardComponent)

                render(spaceCard) {
                    this.space.value = space
                }
            }
        }
    }

    private inline fun SimplePanel.spaceRow(spaces: List<Space>, crossinline block: SimplePanel.() -> Unit) {
        val lgCols = if (spaces.size == 1) 1 else 2

        div(className = "row row-cols-1 row-cols-lg-$lgCols g-3") {
            block()
        }
    }

    private inline fun SimplePanel.col(crossinline block: SimplePanel.() -> Unit) {
        div(className = "col") {
            block()
        }
    }

    private fun fetchAllSpaces() {
        if (!fetchingSpaces.value) {
            launchMonitor(loop = true) {
                getSpaces(offset = 0)?.let { fetchedSpaces ->
                    if (spaces.value != fetchedSpaces) {
                        spaces.value = fetchedSpaces
                    }
                }

                delay(SpaceFetchingDelay)
            }

            fetchingSpaces.value = true
        }
    }

    private fun fetchMoreSpaces() {
        renderingScope.launch {
            paginator.currentPage++
            getSpaces()?.let { fetchedSpaces ->
                spaces.value = FetchedSpaces(spaces.value.first + fetchedSpaces.first, fetchedSpaces.second)
            }
        }
    }

    private suspend fun getSpaces(offset: Int = paginator.offset): FetchedSpaces? =
        beamClient.getSpaces(paginator.limit, offset).getOrNull()?.let { page ->
            val spaces = page.elements

            val rootSpace = spaces.find {
                it.role == Role.ROOT
            }

            val filteredSpaces = spaces.toMutableList().apply {
                rootSpace?.let {
                    remove(it)
                    add(0, it)
                }
            }.filter {
                it.id != currentSpace?.id
            }

            val totalCount = page.totalCount.let {
                if (currentSpace != null) {
                    it - 1
                } else {
                    it
                }
            }

            filteredSpaces to totalCount
        }

    private fun SimplePanel.fetchMoreButton() {
        div(className = "d-flex w-100 justify-content-center mt-2") {
            button(Texts.SpaceListing.FETCH_MORE_BUTTON, style = ButtonStyle.LINK) {
                bindToCurrentTheme {
                    color = currentTheme.secondaryBlue
                }

                onClick {
                    fetchMoreSpaces()
                }
            }
        }
    }

    private companion object {

        private const val PAGE_LIMIT = 4
    }
}