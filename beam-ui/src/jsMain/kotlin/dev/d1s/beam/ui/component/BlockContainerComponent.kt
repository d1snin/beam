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

import dev.d1s.beam.client.BeamClient
import dev.d1s.beam.commons.Block
import dev.d1s.beam.commons.Blocks
import dev.d1s.beam.commons.RowAlign
import dev.d1s.beam.commons.RowIndex
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.contententity.splitBy
import dev.d1s.beam.ui.state.Observable
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

private data class BlockBatch(
    val rowIndex: RowIndex,
    val blocks: Blocks
)

class BlockContainerComponent : Component<Unit>(), KoinComponent {

    private val currentSpaceContentChangeObservable by inject<Observable<Blocks?>>(Qualifier.CurrentSpaceContentChangeObservable)

    private val client by inject<BeamClient>()

    private val clientScope = CoroutineScope(Dispatchers.Main)

    override fun SimplePanel.render(): Effect {
        div().bind(currentSpaceContentChangeObservable.state) { change ->
            change?.let { blocks ->
                renderBlocks(blocks)
            }
        }

        return Effect.Success
    }

    private fun SimplePanel.renderBlocks(blocks: Blocks) {
        println("blockContainer renderBlocks: $blocks")

        div(className = "container-fluid px-0 d-flex justify-content-center") {
            vPanel(className = "w-100") {
                val batches = buildList {
                    blocks.splitBy(selector = { it.row }) { blocks, row ->
                        val batch = BlockBatch(row, blocks.toList())
                        add(batch)
                    }
                }

                processBatches(batches)
            }
        }
    }

    private fun SimplePanel.processBatches(batches: List<BlockBatch>) {
        println("blockContainer processBatches: $batches")

        val maxPaddingCount = batches.getMaxPaddingCount()

        batches.forEachIndexed { batchIndex, blockBatch ->
            val widthCompensator = blockBatch.getWidthCompensator(maxPaddingCount)

            renderBlockPanel(batchIndex, blockBatch, batches, widthCompensator)
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
        compensator: Double
    ) {
        println("blockContainer renderBlockPanel: index $index batch $batch batches $batches compensator $compensator")

        div(className = "w-100") {
            clientScope.launch {
                val currentSpaceId = currentSpace?.id

                if (currentSpaceId != null) {
                    val row = client.getRow(batch.rowIndex, currentSpaceId).getOrThrow()
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