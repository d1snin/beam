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
import dev.d1s.beam.ui.theme.setSecondaryText
import dev.d1s.beam.ui.util.currentTranslation
import dev.d1s.beam.ui.util.iconAlt
import dev.d1s.beam.ui.util.spaceInfoDefaultTitle
import dev.d1s.beam.ui.util.renderSpaceLink
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
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

    override fun SimplePanel.render(): Effect {
        if (config.bare.value) {
            renderCardContent()
        } else {
            renderCardWithContent()
        }

        return Effect.Success
    }

    private fun SimplePanel.renderCardWithContent() {
        val className = "p-${config.cardPaddingLevel.value} ps-${config.cardStartPaddingLevel.value}"

        renderCard(className, usePageBackground = true) {
            renderCardContent()
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
        renderSpaceLink(space) {
            if (space != null) {
                renderImage(space)
            } else {
                renderCurrentSpaceBoundImage()
            }
        }
    }

    private fun SimplePanel.renderCurrentSpaceBoundImage() {
        bind(currentSpaceChangeObservable.state) {
            val receivedSpace = it.space
            renderImage(receivedSpace)
        }
    }

    private fun SimplePanel.renderImage(space: Space?) {
        image(space?.view?.icon ?: ResourceLocation.ICON, alt = currentTranslation.iconAlt) {
            width = config.iconWidth.value
        }
    }

    private fun SimplePanel.renderSpaceInfo(space: Space?) {
        renderLinkedContainer(space) {
            if (space != null) {
                renderSpaceInfoContent(space)
            } else {
                renderCurrentSpaceBoundSpaceInfoContent()
            }
        }
    }

    private fun SimplePanel.renderLinkedContainer(space: Space?, block: SimplePanel.() -> Unit) {
        renderSpaceLink(space) {
            vPanel(justify = JustifyContent.CENTER, className = "ms-3") {
                block()
            }
        }
    }

    private fun SimplePanel.renderCurrentSpaceBoundSpaceInfoContent() {
        bind(currentSpaceChangeObservable.state) {
            val receivedSpace = it.space
            renderSpaceInfoContent(receivedSpace)
        }
    }

    private fun SimplePanel.renderSpaceInfoContent(space: Space?) {
        renderTitle(space)
        renderDescription(space)
    }

    private fun SimplePanel.renderTitle(space: Space?) {
        p(className = "mb-0") {
            val headingClass = if (config.enableHeading.value) {
                "h2"
            } else {
                "fs-bold"
            }

            addCssClass(headingClass)

            val title = space?.view?.title ?: currentTranslation.spaceInfoDefaultTitle

            +title
        }
    }

    private fun SimplePanel.renderDescription(space: Space?) {
        space?.view?.description?.let {
            span {
                fontSize = 0.8.rem

                setSecondaryText()

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