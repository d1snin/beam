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

import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.theme.setOverlay
import dev.d1s.beam.ui.util.*
import dev.d1s.exkt.kvision.bootstrap.*
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import dev.d1s.exkt.kvision.component.LazyEffect
import dev.d1s.exkt.kvision.component.render
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import io.kvision.utils.px
import kotlinx.atomicfu.atomic
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ExploreOffCanvasComponent : Component<Unit>(), KoinComponent {

    private val spaceCardComponent by inject<Component<SpaceCardComponent.Config>>(Qualifier.SpaceCardComponent)

    private val spaceListingComponent by inject<Component<SpaceListingComponent.Config>>(Qualifier.SpaceListingComponent)

    override fun SimplePanel.render(): Effect {
        renderOffCanvas {
            setOverlay()

            id = OFF_CANVAS_ID

            renderOffCanvasBody {
                div {
                    w100()
                    dFlex()
                    justifyContentBetween()
                    alignItemsCenter()
                    mb5()

                    renderSpaceCard()
                    renderDismissButton()
                }

                renderSpaceListingComponent()
            }
        }

        return Effect.Success
    }

    private fun SimplePanel.renderSpaceCard() {
        render(spaceCardComponent) {
            bare.value = true
            cardPaddingLevel.value = 4
            iconWidth.value = 45.px
            enableHeading.value = true
        }
    }

    private fun SimplePanel.renderDismissButton() {
        renderUnstyledLink(url = "#") {
            fs1()
            pe2()

            role = "button"
            setAttribute("data-bs-dismiss", "offcanvas")

            bootstrapIcon(Icons.CLOSE)
        }
    }

    private fun SimplePanel.renderSpaceListingComponent() {
        val effect = render(spaceListingComponent) {
            singleColumn.value = true
        }

        (effect as? LazyEffect)?.state?.subscribe { visible ->
            exploreButton.value?.visible = visible
        }
    }

    companion object {

        private const val OFF_CANVAS_ID = "main-off-canvas"

        private const val OFF_CANVAS_TOGGLE_ID = "main-off-canvas-toggle"

        private var exploreButton = atomic<SimplePanel?>(null)

        fun SimplePanel.renderExploreButton() {
            renderOffCanvasToggle(target = OFF_CANVAS_ID) {
                id = OFF_CANVAS_TOGGLE_ID

                // initially invisible
                visible = false

                exploreButton.value = this
            }
        }
    }
}