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
import dev.d1s.beam.commons.blockBare
import dev.d1s.beam.commons.blockId
import dev.d1s.beam.commons.blockLink
import dev.d1s.beam.commons.contententity.CommonParameters
import dev.d1s.beam.commons.contententity.ContentEntities
import dev.d1s.beam.commons.contententity.ContentEntity
import dev.d1s.beam.ui.contententity.renderEntities
import dev.d1s.beam.ui.contententity.splitBy
import dev.d1s.beam.ui.util.*
import dev.d1s.beam.ui.util.Size.MaxBlockSize
import dev.d1s.beam.ui.util.Size.sizeOf
import dev.d1s.exkt.kvision.bootstrap.*
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import io.kvision.core.onEvent
import io.kvision.html.div
import io.kvision.html.span
import io.kvision.panel.SimplePanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.utils.event
import io.kvision.utils.plus
import io.kvision.utils.px
import kotlinx.atomicfu.atomic
import org.koin.core.component.KoinComponent

class BlockComponent : Component<BlockComponent.Config>(::Config), KoinComponent {

    private val blockMargin = BLOCK_MARGIN_VALUE.px

    private val collapsedContentId
        get() = COLLAPSED_CONTENT_ID + collapsedContentCounter

    private val block by lazy {
        requireNotNull(config.block.value) {
            "Block isn't set"
        }
    }

    override fun SimplePanel.render(): Effect {
        renderBlockCard(block)

        return Effect.Success
    }

    private fun SimplePanel.renderBlockCard(block: Block) {
        var containerConfigured = false

        fun SimplePanel.configureContainer() {
            if (!containerConfigured) {
                applyMargin()
            }

            w100()
            dFlex()

            val blockSize = if (config.single.value) {
                sizeOf(MaxBlockSize).px
            } else {
                sizeOf(block.size).px
            }

            maxWidth = blockSize

            applyCompensator()

            containerConfigured = true
        }

        fun SimplePanel.renderBlockCard() {
            renderStyledCard(bare = block.metadata.blockBare) {
                flexColumn()
                justifyContentStart()
                overflowHidden()

                configureContainer()
                configurePadding(block)

                setOptionalBlockId(block)

                renderContent(block)
            }
        }

        block.metadata.blockLink?.let {
            renderFriendlyLink(url = it, external = true) {
                configureContainer()
                renderBlockCard()
            }
        } ?: renderBlockCard()
    }

    private fun SimplePanel.configurePadding(block: Block) {
        if (block.isFluidImage()) {
            p0()
        } else {
            p3()
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
        block.metadata.blockId?.let {
            if (it.isNotBlank() && !it.contains(" ")) {
                id = it
            }
        }
    }

    private fun SimplePanel.renderContent(block: Block) {
        block.entities.splitCollapsedEntities { batch, collapsed ->
            if (collapsed) {
                renderExpandableContainer {
                    renderEntities(block, batch)
                }
            } else {
                renderEntities(block, batch)
            }
        }
    }

    private fun ContentEntities.splitCollapsedEntities(block: (ContentEntities, collapsed: Boolean) -> Unit) {
        fun ContentEntity.isCollapsed() =
            parameters[CommonParameters.COLLAPSED]?.toBooleanStrictOrNull() ?: false

        splitBy(selector = { entity -> entity.isCollapsed() }) { entities, collapsed ->
            block(entities, collapsed)
        }
    }

    private fun SimplePanel.renderExpandableContainer(block: SimplePanel.() -> Unit) {
        collapsedContentCounter++

        var opened = false
        val clicked = ObservableValue(opened)

        val contentId = collapsedContentId

        div().bind(clicked) {
            renderFriendlyLink(url = "#$contentId") {
                fwBold()

                role = "button"
                setAttribute("data-bs-toggle", "collapse")
                setAttribute("aria-expanded", "false")
                setAttribute("aria-controls", contentId)

                val icon = if (opened) {
                    Icons.CHEVRON_DOWN
                } else {
                    Icons.CHEVRON_RIGHT
                }

                bootstrapIconWithMargin(icon, margin = 1)
                span(currentTranslation.blockCollapsedContentEntityButtonMessage)

                opacity = if (opened) 0.6 else 1.0
            }
        }

        renderCollapsedContainer {
            mt2()

            onEvent {
                event(Events.COLLAPSE_HIDE) {
                    opened = false
                    clicked.value = opened
                }

                event(Events.COLLAPSE_SHOW) {
                    opened = true
                    clicked.value = opened
                }
            }

            id = contentId

            block()
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

        private var collapsedContentCounter = 0

        const val BLOCK_MARGIN_VALUE = 20

        private const val COLLAPSED_CONTENT_ID = "collapsed-content-"
    }
}