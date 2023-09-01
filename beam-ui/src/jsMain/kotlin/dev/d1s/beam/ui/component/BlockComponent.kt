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
import dev.d1s.beam.commons.MetadataKeys
import dev.d1s.beam.ui.contententity.renderEntities
import dev.d1s.beam.ui.util.Size.sizeOf
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import io.kvision.core.CssSize
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import io.kvision.utils.minus
import io.kvision.utils.px
import kotlinx.atomicfu.atomic
import org.koin.core.component.KoinComponent

class BlockComponent : Component<BlockComponent.Config>(::Config), KoinComponent {

    override fun SimplePanel.render(): Effect {
        val block = requireNotNull(config.block.value) {
            "Block isn't set"
        }

        renderBlock(block)

        return Effect.Success
    }

    private fun SimplePanel.renderBlock(block: Block) {
        div(className = "w-100") {
            val blockSize = sizeOf(block.size).px
            maxWidth = blockSize

            applyPadding()
            renderBlockCard(block, blockSize)
        }
    }

    private fun SimplePanel.renderBlockCard(block: Block, blockSize: CssSize) {
        renderCard("w-100 p-4 d-flex flex-column justify-content-start") {
            maxWidth = if (config.applyPaddingEnd.value) blockSize.minus(cardPadding.first) else blockSize

            setOptionalBlockId(block)
            renderEntities(block.entities)
        }
    }

    private fun SimplePanel.applyPadding() {
        if (config.applyPaddingEnd.value) {
            paddingRight = cardPadding
        }

        if (config.applyPaddingBottom.value) {
            paddingBottom = cardPadding
        }
    }

    private fun SimplePanel.setOptionalBlockId(block: Block) {
        val blockElementId = block.metadata[MetadataKeys.UI_BLOCK_ID]

        blockElementId?.let {
            if (it.isNotBlank() && !it.contains(" ")) {
                id = it
            }
        }
    }

    class Config {

        val block = atomic<Block?>(null)

        val applyPaddingEnd = atomic(true)
        val applyPaddingBottom = atomic(true)
    }

    private companion object {

        private val cardPadding = 20.px
    }
}