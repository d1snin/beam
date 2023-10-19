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
import dev.d1s.beam.ui.util.Size.MaxBlockSize
import dev.d1s.beam.ui.util.Size.sizeOf
import dev.d1s.beam.ui.util.isFluidImage
import dev.d1s.beam.ui.util.renderFriendlyLink
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
        val isBare = block.metadata[MetadataKeys.UI_BLOCK_BARE]
            ?.toBooleanStrictOrNull() == true

        fun SimplePanel.configureContainer() {
            addCssClass("w-100")
            addCssClass("d-flex")

            val blockSize = if (config.single.value) {
                sizeOf(MaxBlockSize).px
            } else {
                sizeOf(block.size).px
            }

            maxWidth = blockSize

        }

        fun SimplePanel.renderCard() {
            renderCard("flex-column justify-content-start", bare = isBare) {
                configureContainer()

                configurePadding(block)

                applyMargin()
                applyCompensator()

                setOptionalBlockId(block)

                renderEntities(block)
            }
        }

        val link = block.metadata[MetadataKeys.UI_BLOCK_LINK]

        link?.let {
            renderFriendlyLink(url = it, external = true) {
                configureContainer()

                renderCard()
            }
        } ?: renderCard()
    }

    private fun SimplePanel.configurePadding(block: Block) {
        if (block.isFluidImage()) {
            addCssClass("p-0")
            addCssClass("overflow-hidden")
        } else {
            addCssClass("p-3")
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

        val single = atomic(false)
    }

    companion object {

        const val BLOCK_MARGIN_VALUE = 20

        private val blockMargin = BLOCK_MARGIN_VALUE.px
    }
}