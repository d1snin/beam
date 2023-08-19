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
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.resource.ResourceLocation
import dev.d1s.beam.ui.state.CurrentSpaceChange
import dev.d1s.beam.ui.state.Observable
import dev.d1s.beam.ui.state.bindToCurrentTheme
import dev.d1s.beam.ui.theme.currentTheme
import dev.d1s.beam.ui.util.Texts
import dev.d1s.beam.ui.util.currentSpaceLink
import dev.d1s.exkt.kvision.component.Component
import io.kvision.core.JustifyContent
import io.kvision.html.div
import io.kvision.html.image
import io.kvision.html.p
import io.kvision.html.span
import io.kvision.panel.SimplePanel
import io.kvision.panel.vPanel
import io.kvision.state.bind
import io.kvision.utils.px
import io.kvision.utils.rem
import kotlinx.atomicfu.atomic
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SpaceCardComponent : Component<SpaceCardComponent.Config>(::Config), KoinComponent {

    private val currentSpaceChangeObservable by inject<Observable<CurrentSpaceChange>>(Qualifier.CurrentSpaceChangeObservable)

    override fun SimplePanel.render() {
        if (config.bare.value) {
            renderCardContent()
        } else {
            card(
                "p-${config.cardPaddingLevel.value} ps-${config.cardStartPaddingLevel.value}",
                backgroundColor = { currentTheme.background }) {
                renderCardContent()
            }
        }
    }

    // Господи. Мне страшно. Я боюсь за все.
    // За себя. За нее. За наше будущее.
    // Что с нами будет?

    private fun SimplePanel.renderCardContent() {
        div(className = "d-flex align-items-center") {
            val space = config.space.value

            renderIcon(space)
            renderSpaceInfo(space)
        }
    }

    private fun SimplePanel.renderIcon(space: Space?) {
        currentSpaceLink {
            if (space != null) {
                renderImage(space)
            } else {
                bind(currentSpaceChangeObservable.state) {
                    val receivedSpace = it.space
                    renderImage(receivedSpace)
                }
            }
        }
    }

    private fun SimplePanel.renderImage(space: Space?) {
        image(space?.view?.icon ?: ResourceLocation.ICON, alt = Texts.Heading.Icon.ALT) {
            width = config.iconWidth.value
        }
    }

    private fun SimplePanel.renderSpaceInfo(space: Space?) {
        currentSpaceLink {
            vPanel(justify = JustifyContent.CENTER, className = "ms-3") {
                if (space != null) {
                    renderSpaceInfoContent(space)
                } else {
                    bind(currentSpaceChangeObservable.state) {
                        val receivedSpace = it.space
                        renderSpaceInfoContent(receivedSpace)
                    }
                }
            }
        }
    }

    private fun SimplePanel.renderSpaceInfoContent(space: Space?) {
        p(className = "mb-0") {
            if (config.enableHeading.value) {
                addCssClass("h2")
            } else {
                addCssClass("fs-bold")
            }

            +(space?.view?.title ?: Texts.Heading.SpaceInfo.DEFAULT_TITLE)
        }

        space?.view?.description?.let {
            span {
                fontSize = 0.8.rem

                bindToCurrentTheme {
                    color = currentTheme.secondaryText
                }

                +it
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