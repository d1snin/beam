/*
 * Copyright 2023-2024 Mikhail Titov
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

import dev.d1s.beam.client.BeamClient
import dev.d1s.beam.commons.*
import dev.d1s.beam.ui.component.BlockContainerComponent
import dev.d1s.beam.ui.util.*
import dev.d1s.exkt.common.pagination.Paginator
import dev.d1s.exkt.kvision.bootstrap.dFlex
import dev.d1s.exkt.kvision.bootstrap.justifyContentCenter
import dev.d1s.exkt.kvision.bootstrap.mt5
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class SpaceContentChange(
    val rows: Rows,
    val blocks: List<Block>
) {
    fun row(rowIndex: RowIndex) =
        rows.find {
            it.index == rowIndex
        }
}

infix fun Rows?.with(blocks: List<Block>?) =
    this?.let { r ->
        blocks?.let { b ->
            SpaceContentChange(r, b)
        }
    }

private val showEndOfContentSpinner = ObservableValue(true)
private val endOfContent = ObservableValue<SimplePanel?>(null)

class CurrentSpaceContentChangeObservable : Observable<SpaceContentChange?>, KoinComponent {

    override val state = ObservableValue(currentRows with currentBlocks)

    private val client by inject<BeamClient>()

    private val paginator = Paginator(pageLimit = BlockContainerComponent.PAGE_SIZE, currentPage = 1)

    private val renderingScope = CoroutineScope(Dispatchers.Main)

    private val endOfContentLastOnScreenState = atomic(false)

    private val enableOnScrollHandler = atomic(true)

    override fun monitor() =
        launchMonitor {
            val space = currentSpace

            if (space != null) {
                val spaceId = space.id

                handleSpaceContentUpdates(spaceId)
                handleEndOfContent(spaceId)
            } else {
                setCurrentSpaceContent(change = null)
            }
        }

    fun setCurrentSpaceContent(change: SpaceContentChange?) {
        setCurrentSpaceRows(change?.rows)
        setCurrentSpaceBlocks(change?.blocks)
        state.value = change
    }

    private fun handleEndOfContent(spaceId: SpaceId) {
        endOfContent.subscribeSkipping { _ ->
            handleEndOfContentOnScreen(spaceId)

            onScrollOrResize {
                if (enableOnScrollHandler.value) {
                    handleEndOfContentOnScreen(spaceId)
                }
            }
        }
    }

    private fun handleEndOfContentOnScreen(spaceId: SpaceId) {
        val element = endOfContent.value?.getElement() ?: return

        fun handle() {
            val previouslyOnScreen = endOfContentLastOnScreenState.value

            if (!previouslyOnScreen) {
                renderingScope.launch {
                    actualizeCurrentSpaceContent(spaceId)
                }

                endOfContentLastOnScreenState.value = true
            }
        }

        if (element.isOnScreen) {
            handle()
            showEndOfContentSpinner.value = false
        } else {
            endOfContentLastOnScreenState.value = false
        }
    }

    private suspend fun handleSpaceContentUpdates(spaceId: SpaceId) {
        handleBlockCreation(spaceId)
        handleBlockUpdate(spaceId)
        handleBlockRemoval(spaceId)

        handleRowUpdate(spaceId)
    }

    private suspend fun handleBlockCreation(spaceId: SpaceId) {
        client.onBlockCreated {
            val block = it.data

            ifSpaceMatches(block, spaceId) {
                processSpaceContentUpdate(spaceId)
            }
        }
    }

    private suspend fun handleBlockUpdate(spaceId: SpaceId) {
        client.onBlockUpdated { event ->
            val block = event.data.new

            ifSpaceMatches(block, spaceId) {
                processSpaceContentUpdate(spaceId)
            }
        }
    }

    private suspend fun handleBlockRemoval(spaceId: SpaceId) {
        client.onBlockRemoved {
            val block = it.data

            ifSpaceMatches(block, spaceId) {
                processSpaceContentUpdate(spaceId)
            }
        }
    }

    private suspend fun handleRowUpdate(spaceId: SpaceId) {
        client.onRowUpdated {
            val row = it.data.new

            ifSpaceMatches(row, spaceId) {
                processSpaceContentUpdate(spaceId)
            }
        }
    }

    private suspend fun processSpaceContentUpdate(space: SpaceId) {
        actualizeCurrentSpaceContent(space, reload = true)
        handleEndOfContentOnScreen(space)
    }

    private suspend fun actualizeCurrentSpaceContent(space: SpaceId, reload: Boolean = false) {
        suspend fun fetchBlocks() =
            client.getBlocks(space, paginator.limitAndOffset, currentLanguageCode).getOrNull()?.elements ?: listOf()

        suspend fun setSpaceContent(blocks: List<Block>) {
            val rows = client.getRows(space).getOrNull()

            val change = rows with blocks

            setCurrentSpaceContent(change)
        }

        if (reload) {
            val blocks = buildList {
                (1..paginator.currentPage).forEach { page ->
                    paginator.currentPage = page

                    val fetchedBlocks = fetchBlocks()

                    addAll(fetchedBlocks)
                }

                enableOnScrollHandler.value = true
            }

            setSpaceContent(blocks)
        } else {
            if (enableOnScrollHandler.value) {
                paginator.currentPage++
            }

            val fetchedBlocks = fetchBlocks()

            val fetchedBlocksNotEmpty = fetchedBlocks.isNotEmpty()

            enableOnScrollHandler.value = fetchedBlocksNotEmpty
            showEndOfContentSpinner.value = fetchedBlocksNotEmpty

            if (fetchedBlocksNotEmpty) {
                val blocks = (currentBlocks ?: listOf()) + fetchedBlocks
                setSpaceContent(blocks)
            }
        }
    }

    private inline fun ifSpaceMatches(block: Block, spaceId: SpaceId, handler: () -> Unit) {
        if (block.spaceId == spaceId) {
            handler()
        }
    }

    private inline fun ifSpaceMatches(row: Row, spaceId: SpaceId, handler: () -> Unit) {
        if (row.spaceId == spaceId) {
            handler()
        }
    }
}

fun SimplePanel.renderEndOfContent() {
    div().bind(showEndOfContentSpinner) { show ->
        if (show) {
            mt5()
            dFlex()
            justifyContentCenter()

            renderSpinner()
        }

        addAfterInsertHook {
            endOfContent.value = this
        }
    }
}