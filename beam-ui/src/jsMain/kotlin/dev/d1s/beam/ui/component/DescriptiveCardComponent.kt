/*
 * Copyright 2023-2024 Mikhail Titov
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

import dev.d1s.beam.ui.contententity.renderStyledText
import dev.d1s.beam.ui.theme.setSecondaryText
import dev.d1s.beam.ui.util.Events
import dev.d1s.beam.ui.util.renderUnstyledLink
import dev.d1s.exkt.kvision.bootstrap.*
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import io.kvision.core.JustifyContent
import io.kvision.core.TextDecoration
import io.kvision.core.TextDecorationLine
import io.kvision.core.onEvent
import io.kvision.html.div
import io.kvision.html.p
import io.kvision.html.span
import io.kvision.panel.SimplePanel
import io.kvision.panel.vPanel
import io.kvision.utils.event
import io.kvision.utils.rem
import kotlinx.atomicfu.atomic
import org.koin.core.component.KoinComponent

class DescriptiveCardComponent : Component<DescriptiveCardComponent.Config>(::Config), KoinComponent {

    private var linkedContainer: SimplePanel? = null

    override fun SimplePanel.render(): Effect {
        if (config.bare.value) {
            renderContent()
        } else {
            renderContentCard()
        }

        return Effect.Success
    }

    private fun SimplePanel.renderContentCard() {
        this@renderContentCard.renderStyledCard(usePageBackground = true) {
            dFlex()
            alignItemsCenter()

            addCssClass("p-${config.cardPaddingLevel.value}")
            addCssClass("ps-${config.cardStartPaddingLevel.value}")

            if (config.fullWidth.value) w100()
            if (config.fullHeight.value) h100()

            renderContent()
        }
    }

    private fun SimplePanel.renderContent() {
        renderOptionallyLinkedContainer {
            renderImage()
            renderSpaceInfo()

            if (!config.bare.value) {
                renderIcon()
            }
        }
    }

    private fun SimplePanel.renderOptionallyLinkedContainer(block: SimplePanel.() -> Unit) {
        renderOptionalLink(fullWidth = true) {
            div {
                dFlex()
                alignItemsCenter()

                linkedContainer = this

                block()
            }
        }
    }

    private fun SimplePanel.renderIcon() {
        config.icon.value?.let { icon ->
            div {
                msAuto()
                me2()

                fontSize = 1.2.rem

                bootstrapIcon(icon)
            }
        }
    }

    private fun SimplePanel.renderImage() {
        val image = config.imageInit.value

        image?.let {
            renderOptionalLink {
                div {
                    dFlex()
                    h100()
                    justifyContentCenter()
                    alignItemsCenter()

                    image()
                }
            }
        }
    }

    private fun SimplePanel.renderSpaceInfo() {
        renderVPanel {
            renderTitle()
            renderDescription()
        }
    }

    private fun SimplePanel.renderVPanel(block: SimplePanel.() -> Unit) {
        vPanel(justify = JustifyContent.CENTER) {
            mx3()

            block()
        }
    }

    private fun SimplePanel.renderTitle() {
        p {
            mb0()

            if (config.enableHeading.value) {
                h2()
            } else {
                fwBold()
            }

            val title = config.title.value ?: error("Title is not set")
            renderStyledText(title)

            underlineOnHover()
        }
    }

    private fun SimplePanel.renderDescription() {
        config.description.value?.let { description ->
            span {
                fontSize = 0.8.rem

                setSecondaryText()

                renderStyledText(description)
            }
        }
    }

    private fun SimplePanel.underlineOnHover() {
        linkedContainer?.onEvent {
            event(Events.MOUSEENTER) {
                textDecoration = TextDecoration(TextDecorationLine.UNDERLINE)
            }

            event(Events.MOUSELEAVE) {
                textDecoration = TextDecoration(TextDecorationLine.NONE)
            }
        }
    }

    private fun SimplePanel.renderOptionalLink(fullWidth: Boolean = false, block: SimplePanel.() -> Unit) {
        val url = config.url.value

        if (url != null) {
            renderUnstyledLink(
                url = url,
                external = config.external.value,
                download = config.download.value,
                downloadName = config.downloadName.value
            ) {
                if (fullWidth) {
                    w100()
                }

                block()
            }
        } else {
            block()
        }
    }

    class Config {

        val title = atomic<String?>(null)

        val description = atomic<String?>(null)

        val url = atomic<String?>(null)

        val external = atomic(false)

        val download = atomic(false)

        val downloadName = atomic<String?>(null)

        val bare = atomic(false)

        val cardPaddingLevel = atomic(2)
        val cardStartPaddingLevel = atomic(3)

        val enableHeading = atomic(false)

        val fullWidth = atomic(false)
        val fullHeight = atomic(false)

        val imageInit = atomic<(SimplePanel.() -> Unit)?>(null)

        val icon = atomic<String?>(null)

        fun image(init: SimplePanel.() -> Unit) {
            imageInit.value = init
        }
    }
}