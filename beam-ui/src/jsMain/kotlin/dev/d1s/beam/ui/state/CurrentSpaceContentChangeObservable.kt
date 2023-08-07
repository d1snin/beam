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
import dev.d1s.beam.commons.Block
import dev.d1s.beam.commons.Blocks
import dev.d1s.beam.commons.Space
import dev.d1s.beam.commons.SpaceId
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.util.currentBlocks
import dev.d1s.beam.ui.util.setCurrentSpaceBlocks
import io.kvision.state.ObservableValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class CurrentSpaceContentChange(
    val blocks: Blocks? = null,
    val addedBlock: Block? = null,
    val updatedBlock: Block? = null,
    val removedBlock: Block? = null
)

class CurrentSpaceContentChangeObservable : Observable<CurrentSpaceContentChange>, KoinComponent {

    override val launchOnStartup = true

    override val state = ObservableValue(
        CurrentSpaceContentChange(currentBlocks)
    )

    private val client by inject<PublicBeamClient>()

    private val currentSpaceChangeObservable by inject<Observable<CurrentSpaceChange>>(Qualifier.CurrentSpaceChangeObservable)

    private var handlerScope = CoroutineScope(Dispatchers.Main)

    override fun monitor() =
        launchMonitor {
            currentSpaceChangeObservable.state.subscribe {
                val space = it.space

                if (space != null) {
                    setCurrentBlocks(space)
                    handleSpaceContentUpdates(space.id)
                } else {
                    val change = CurrentSpaceContentChange(blocks = null)
                    setCurrentSpaceContentChange(change)
                }
            }
        }

    private fun setCurrentBlocks(space: Space) {
        handlerScope.launch {
            val blocks = client.getBlocks(space.id).getOrNull()
            val change = CurrentSpaceContentChange(blocks)

            setCurrentSpaceContentChange(change)
        }
    }

    private fun handleSpaceContentUpdates(spaceId: SpaceId) {
        handlerScope.launch {
            handleBlockCreation(spaceId)
            handleBlockUpdate(spaceId)
            handleBlockRemoval(spaceId)
        }
    }

    private suspend fun handleBlockCreation(spaceId: SpaceId) {
        client.onBlockCreated {
            val block = it.data

            handleEvent(block, spaceId) {
                val blocks = state.value.blocks.modifyBlocks {
                    add(block)
                }

                val change = CurrentSpaceContentChange(
                    blocks,
                    addedBlock = block
                )

                setCurrentSpaceContentChange(change)
            }
        }
    }

    private suspend fun handleBlockUpdate(spaceId: SpaceId) {
        client.onBlockUpdated {
            val block = it.data.new

            handleEvent(block, spaceId) {
                val blocks = state.value.blocks

                val change = CurrentSpaceContentChange(
                    blocks,
                    updatedBlock = block
                )

                setCurrentSpaceContentChange(change)
            }
        }
    }

    // - Ты хочешь со мной жить?
    // - Не знаю...
    //   . . .
    //   Ты хочешь знать почему?
    // - Ну... да.
    // - Потому что я вообще жить не хочу
    // ДА НЕПРАВДА ЭТО
    // ГЛУПАЯ ЛОЖЬ
    // Я ТЕБЯ ВИЖУ! ТЫ НЕ ТАКАЯ! НЕТ!
    // Может быть, я просто не хочу принять факт?
    // Возможно, я допустил ошибку?
    // Последней сладости яд, который мучительно убивает.
    // Отказаться невозможно.
    // Должен ли я искать? Должен ли я решать, строить
    // и видеть это будущее в свете?
    // Не устану ли?
    //
    // Я тебя люблю. Я хочу, чтобы у нас все было хорошо.
    // Да. У нас все хорошо. Мы молодцы. Но почему ты именно такая?
    // Это ты. Я полюбил тебя именно такой. Ты мне нравишься всей.
    // Я хочу тебя радовать, ценить и уважать. Хочу получать взамен.
    // Хочу мечтать с тобой и смотреть правде в глаза не боясь трудностей.
    // Жить на полную катушку и не смотреть вниз. Строить великое.
    // Ты достойна большего. Ты достойна всего, о чем когда-либо мечтала.
    // Нет какой-то конкретной причины. Просто есть ты и моя неугасающая любовь.
    // Я говорю себе - время исправит. Мы через все пройдем.
    // Не останавливаться. Не бежать назад.
    //
    // Я боюсь. Но я принимаю вызов. Я не сдамся.

    private suspend fun handleBlockRemoval(spaceId: SpaceId) {
        client.onBlockRemoved {
            val block = it.data

            handleEvent(block, spaceId) {
                val blocks = state.value.blocks.modifyBlocks {
                    remove(block)
                }

                val change = CurrentSpaceContentChange(
                    blocks,
                    removedBlock = block
                )

                setCurrentSpaceContentChange(change)
            }
        }
    }

    private inline fun handleEvent(block: Block, spaceId: SpaceId, handler: () -> Unit) {
        if (block.spaceId == spaceId) {
            handler()
        }
    }

    private inline fun Blocks?.modifyBlocks(modification: MutableList<Block>. () -> Unit): Blocks =
        this?.let {
            this.toMutableList().apply(modification)
        } ?: listOf()

    private fun setCurrentSpaceContentChange(change: CurrentSpaceContentChange) {
        setCurrentSpaceBlocks(change.blocks)
        state.setState(change)
    }
}