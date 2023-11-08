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

import dev.d1s.beam.client.BeamClient
import dev.d1s.beam.commons.*
import dev.d1s.beam.ui.util.*
import io.kvision.state.ObservableValue
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class SpaceContentChange(
    val rows: Rows,
    val blocks: Blocks
) {
    fun row(rowIndex: RowIndex) =
        rows.first {
            it.index == rowIndex
        }
}

infix fun Rows?.with(blocks: Blocks?) =
    this?.let { r ->
        blocks?.let { b ->
            SpaceContentChange(r, b)
        }
    }

class CurrentSpaceContentChangeObservable : Observable<SpaceContentChange?>, KoinComponent {

    override val state = ObservableValue(currentRows with currentBlocks)

    private val client by inject<BeamClient>()

    override fun monitor() =
        launchMonitor {
            val space = currentSpace

            if (space != null) {
                handleSpaceContentUpdates(space.id)
            } else {
                setCurrentSpaceContent(change = null)
            }
        }

    fun setCurrentSpaceContent(change: SpaceContentChange?) {
        setCurrentSpaceRows(change?.rows)
        setCurrentSpaceBlocks(change?.blocks)
        state.setState(change)
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
                actualizeCurrentSpaceContent(spaceId)
            }
        }
    }

    private suspend fun handleBlockUpdate(spaceId: SpaceId) {
        client.onBlockUpdated { event ->
            val block = event.data.new

            ifSpaceMatches(block, spaceId) {
                actualizeCurrentSpaceContent(spaceId)
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

            ifSpaceMatches(block, spaceId) {
                actualizeCurrentSpaceContent(spaceId)
            }
        }
    }

    private suspend fun handleRowUpdate(spaceId: SpaceId) {
        client.onRowUpdated {
            val row = it.data.new

            ifSpaceMatches(row, spaceId) {
                actualizeCurrentSpaceContent(spaceId)
            }
        }
    }

    private suspend fun actualizeCurrentSpaceContent(space: SpaceId) {
        val rows = client.getRows(space).getOrNull()
        val blocks = client.getBlocks(space, currentLanguageCode).getOrNull()

        val change = rows with blocks

        setCurrentSpaceContent(change)
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