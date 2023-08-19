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
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.state.Observable
import dev.d1s.beam.ui.util.Size
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.render
import io.kvision.core.AlignItems
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

class BlockContainerComponent : Component<Unit>(), KoinComponent {

    private val currentSpaceContentChangeObservable by inject<Observable<Blocks?>>(Qualifier.CurrentSpaceContentChangeObservable)

    override fun SimplePanel.render() {
        div(className = "mb-5").bind(currentSpaceContentChangeObservable.state) { change ->
            change?.let { blocks ->
                renderBlocks(blocks)
            }
        }
    }

    private fun SimplePanel.renderBlocks(blocks: Blocks) {
        div(className = "container-fluid d-flex justify-content-center mb-5") {
            vPanel(justify = JustifyContent.CENTER, alignItems = AlignItems.CENTER) {
                val batches = blocks.splitIntoBatches()

                batches.forEachIndexed { batchIndex, blockBatch ->
                    hPanel {
                        blockBatch.forEachIndexed { blockIndex, block ->
                            val lastBlock = blockIndex == blockBatch.lastIndex
                            val lastBatch = batchIndex == batches.lastIndex

                            renderBlock(block, lastBlock, lastBatch)
                        }
                    }
                }
            }
        }
    }

    private fun Blocks.splitIntoBatches(): List<Blocks> {
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

        forEach { block ->
            if (currentBatch.totalSizeIncluding(block) > maxBlockSize) {
                processCurrentBatch()
            }

            currentBatch += block
        }

        processCurrentBatch()

        return batches
    }

    private fun SimplePanel.renderBlock(block: Block, lastBlock: Boolean, lastBatch: Boolean) {
        val blockComponent = get<Component<BlockComponent.Config>>(Qualifier.BlockComponent)

        render(blockComponent) {
            this.block.value = block

            if (lastBlock) {
                this.applyPaddingEnd.value = false
            }

            if (lastBatch) {
                this.applyPaddingBottom.value = false
            }
        }
    }
}