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

import dev.d1s.beam.commons.Block
import dev.d1s.beam.commons.Blocks
import dev.d1s.beam.commons.RowAlign
import dev.d1s.beam.commons.RowIndex
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.contententity.splitBy
import dev.d1s.beam.ui.state.Observable
import dev.d1s.beam.ui.state.SpaceContentChange
import dev.d1s.beam.ui.util.Size
import dev.d1s.beam.ui.util.currentSpace
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import dev.d1s.exkt.kvision.component.render
import io.kvision.core.JustifyContent
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import io.kvision.panel.hPanel
import io.kvision.panel.vPanel
import io.kvision.state.bind
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import kotlin.math.min

private data class BlockBatch(
    val rowIndex: RowIndex,
    val blocks: Blocks
)

class BlockContainerComponent : Component<Unit>(), KoinComponent {

    private val currentSpaceContentChangeObservable by inject<Observable<SpaceContentChange?>>(Qualifier.CurrentSpaceContentChangeObservable)

    override fun SimplePanel.render(): Effect {
        div().bind(currentSpaceContentChangeObservable.state) { change ->
            change?.let {
                renderBlocks(it)
            }
        }

        return Effect.Success
    }

    private fun SimplePanel.renderBlocks(change: SpaceContentChange) {
        div(className = "container-fluid px-0 d-flex justify-content-center") {
            vPanel(className = "w-100") {
                val batches = change.blocks.splitIntoBatches()
                processBatches(batches, change)
            }
        }
    }

    private fun Blocks.splitIntoBatches(): List<BlockBatch> =
        buildList {
            splitIntoBatchesByRow().forEach {
                it.splitIntoBatchesBySize().forEach { batchBySize ->
                    add(batchBySize)
                }
            }
        }

    private fun Blocks.splitIntoBatchesByRow(): List<BlockBatch> {
        val blocks = this@splitIntoBatchesByRow

        val rowBatches = buildList {
            blocks.splitBy(selector = { it.row }) { blocks, row ->
                val batch = BlockBatch(row, blocks.toList())
                add(batch)
            }
        }

        return rowBatches
    }


    private fun BlockBatch.splitIntoBatchesBySize(): List<BlockBatch> {
        val maxBlockSize = Size.MaxBlockSize.level

        val batches = mutableListOf<Blocks>()
        val currentBatch = mutableListOf<Block>()

        fun Block.relativeSize() =
            min(size.level, maxBlockSize)

        fun Blocks.totalSizeIncluding(block: Block) =
            sumOf {
                it.relativeSize()
            } + block.relativeSize()

        fun processCurrentBatch() {
            batches += currentBatch.toMutableList()
            currentBatch.clear()
        }

        blocks.forEach { block ->
            if (currentBatch.totalSizeIncluding(block) > maxBlockSize) {
                processCurrentBatch()
            }

            currentBatch += block
        }

        processCurrentBatch()

        return batches.map {
            BlockBatch(rowIndex, it)
        }
    }

    private fun SimplePanel.processBatches(batches: List<BlockBatch>, change: SpaceContentChange) {
        val maxPaddingCount = batches.getMaxPaddingCount()

        batches.forEachIndexed { batchIndex, blockBatch ->
            val widthCompensator = blockBatch.getWidthCompensator(maxPaddingCount)

            renderBlockPanel(batchIndex, blockBatch, batches, widthCompensator, change)
        }
    }

    private fun List<BlockBatch>.getMaxPaddingCount() =
        maxOf {
            it.blocks.getPaddingCount()
        }

    private fun Blocks.getPaddingCount() =
        size - 1

    private fun BlockBatch.getWidthCompensator(maxPaddingCount: Int): Double {
        val paddingCount = blocks.getPaddingCount()

        return if (paddingCount < maxPaddingCount) {
            val compensatorMultiplier = maxPaddingCount - paddingCount
            val totalCompensator = BlockComponent.BLOCK_MARGIN_VALUE * compensatorMultiplier
            totalCompensator.toDouble() / blocks.size
        } else {
            .0
        }
    }

    private fun SimplePanel.renderBlockPanel(
        index: Int,
        batch: BlockBatch,
        batches: List<BlockBatch>,
        compensator: Double,
        change: SpaceContentChange
    ) {
        val currentSpaceId = currentSpace?.id

        if (currentSpaceId != null) {
            val row = change.row(batch.rowIndex)
            val justify = justifyContentByRowAlign(row.align)

            hPanel(className = "w-100", justify = justify) {
                val blocks = batch.blocks

                blocks.forEachIndexed { blockIndex, block ->
                    val lastBlock = blockIndex == blocks.lastIndex
                    val lastBatch = index == batches.lastIndex

                    val single = blocks.size == 1

                    renderBlock(block, lastBlock, lastBatch, compensator, single)
                }
            }
        }
    }

    private fun SimplePanel.renderBlock(
        block: Block,
        lastBlock: Boolean,
        lastBatch: Boolean,
        compensator: Double,
        single: Boolean
    ) {
        val blockComponent = get<Component<BlockComponent.Config>>(Qualifier.BlockComponent)

        render(blockComponent) {
            this.block.value = block

            if (lastBlock) {
                this.applyMarginEnd.value = false
            }

            if (lastBatch) {
                this.applyMarginBottom.value = false
            }

            this.widthCompensator.value = compensator

            this.single.value = single
        }
    }

    private fun justifyContentByRowAlign(align: RowAlign) =
        when (align) {
            RowAlign.START -> JustifyContent.START
            RowAlign.END -> JustifyContent.END
            RowAlign.CENTER -> JustifyContent.CENTER
            RowAlign.BETWEEN -> JustifyContent.SPACEBETWEEN
        }
}