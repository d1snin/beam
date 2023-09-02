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
import io.kvision.panel.SimplePanel
import io.kvision.utils.plus
import io.kvision.utils.px
import kotlinx.atomicfu.atomic
import org.koin.core.component.KoinComponent

class BlockComponent : Component<BlockComponent.Config>(::Config), KoinComponent {

    override fun SimplePanel.render(): Effect {
        val block = requireNotNull(config.block.value) {
            "Block isn't set"
        }

        renderBlockCard(block)

        return Effect.Success
    }

    private fun SimplePanel.renderBlockCard(block: Block) {
        renderCard("w-100 p-4 d-flex flex-column justify-content-start") {
            val blockSize = sizeOf(block.size).px
            maxWidth = blockSize

            applyMargin()
            applyCompensator()

            setOptionalBlockId(block)

            renderEntities(block.entities)
        }
    }

    private fun SimplePanel.applyMargin() {
        if (config.applyMarginEnd.value) {
            marginRight = blockMargin
        }

        if (config.applyMarginBottom.value) {
            marginBottom = blockMargin
        }
    }

    private fun SimplePanel.applyCompensator() {
        maxWidth += config.widthCompensator.value
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

        val applyMarginEnd = atomic(true)
        val applyMarginBottom = atomic(true)

        val widthCompensator = atomic(.0)
    }

    companion object {

        const val BLOCK_MARGIN_VALUE = 20

        private val blockMargin = BLOCK_MARGIN_VALUE.px
    }
}