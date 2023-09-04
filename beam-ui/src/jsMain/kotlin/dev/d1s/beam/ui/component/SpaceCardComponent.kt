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

import dev.d1s.beam.commons.Space
import dev.d1s.beam.ui.resource.ResourceLocation
import dev.d1s.beam.ui.theme.setSecondaryText
import dev.d1s.beam.ui.util.*
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import io.kvision.core.*
import io.kvision.html.*
import io.kvision.panel.SimplePanel
import io.kvision.panel.vPanel
import io.kvision.utils.event
import io.kvision.utils.px
import io.kvision.utils.rem
import kotlinx.atomicfu.atomic
import org.koin.core.component.KoinComponent

class SpaceCardComponent : Component<SpaceCardComponent.Config>(::Config), KoinComponent {

    private val space by lazy {
        config.space.value ?: currentSpace
    }

    private var linkedContainer: SimplePanel? = null

    override fun SimplePanel.render(): Effect {
        if (config.bare.value) {
            renderContent()
        } else {
            renderCard()
        }

        return Effect.Success
    }

    private fun SimplePanel.renderCard() {
        val className = "p-${config.cardPaddingLevel.value} ps-${config.cardStartPaddingLevel.value}"

        this@renderCard.renderCard(className, usePageBackground = true) {
            renderContent()
        }
    }

    // Господи. Мне страшно. Я боюсь за все.
    // За себя. За нее. За наше будущее.
    // Что с нами будет?

    private fun SimplePanel.renderContent() {
        renderLinkedContainer {
            renderIcon()
            renderSpaceInfo()

            if (!config.bare.value) {
                renderOpenIcon()
            }
        }
    }

    private fun SimplePanel.renderLinkedContainer(block: SimplePanel.() -> Unit) {
        renderSpaceLink {
            div(className = "d-flex align-items-center") {
                linkedContainer = this

                block()
            }
        }
    }

    private fun SimplePanel.renderOpenIcon() {
        div(className = "ms-auto me-2") {
            fontSize = 1.2.rem

            icon("bi bi-box-arrow-up-right")
        }
    }

    private fun SimplePanel.renderIcon() {
        renderSpaceLink(space) {
            renderImage()
        }
    }

    private fun SimplePanel.renderImage() {
        image(space?.view?.icon ?: ResourceLocation.ICON, alt = currentTranslation.iconAlt) {
            width = config.iconWidth.value
        }
    }

    private fun SimplePanel.renderSpaceInfo() {
        renderVPanel {
            renderTitle()
            renderDescription()
        }
    }

    private fun SimplePanel.renderVPanel(block: SimplePanel.() -> Unit) {
        vPanel(justify = JustifyContent.CENTER, className = "ms-3") {
            block()
        }
    }

    private fun SimplePanel.renderTitle() {
        p(className = "mb-0") {
            val headingClass = if (config.enableHeading.value) {
                "h2"
            } else {
                "fs-bold"
            }

            addCssClass(headingClass)

            val title = space?.view?.title ?: currentTranslation.spaceInfoDefaultTitle
            +title

            underlineOnHover()
        }
    }

    private fun SimplePanel.renderDescription() {
        space?.view?.description?.let {
            span {
                fontSize = 0.8.rem

                setSecondaryText()

                +it
            }
        }
    }

    private fun SimplePanel.underlineOnHover() {
        linkedContainer?.onEvent {
            event("mouseenter") {
                textDecoration = TextDecoration(TextDecorationLine.UNDERLINE)
            }

            event("mouseleave") {
                textDecoration = TextDecoration(TextDecorationLine.NONE)
            }
        }
    }

    class Config {

        val space = atomic<Space?>(null)

        val bare = atomic(false)

        val cardPaddingLevel = atomic(2)
        val cardStartPaddingLevel = atomic(3)

        val iconWidth = atomic(30.px)

        val enableHeading = atomic(false)
    }
}