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
import dev.d1s.beam.ui.component.ExploreOffCanvasComponent.Companion.renderExploreButton
import dev.d1s.beam.ui.util.renderFluidContainer
import dev.d1s.exkt.kvision.bootstrap.*
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import dev.d1s.exkt.kvision.component.render
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import io.kvision.utils.px
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class HeadingComponent : Component<Unit>(), KoinComponent {

    override fun SimplePanel.render(): Effect {
        renderFluidContainer {
            addCssClass("sticky-top")

            setBlur()

            px3()
            py2()
            mb4()
            dFlex()
            flexColumn()
            flexRow(breakpoint = Breakpoint.LG)
            justifyContentBetween(breakpoint = Breakpoint.LG)

            renderSpaceHeading()
        }

        return Effect.Success
    }

    private fun SimplePanel.renderSpaceHeading() {
        div {
            dFlex()
            alignItemsCenter()
            justifyContentBetween()
            justifyContentStart(breakpoint = Breakpoint.MD)

            renderExploreButton()
            renderSpaceCard()
        }
    }

    private fun SimplePanel.renderSpaceCard() {
        val spaceCard = get<Component<SpaceCardComponent.Config>>(Qualifier.SpaceCardComponent)

        render(spaceCard) {
            bare.value = true
            cardPaddingLevel.value = 4
            iconWidth.value = 45.px
            enableHeading.value = true
        }
    }

    private fun SimplePanel.setBlur() {
        setStyle("-webkit-backdrop-filter", "blur($BLUR_RADIUS)")
        setStyle("backdrop-filter", "blur($BLUR_RADIUS)")
    }

    private companion object {

        private const val BLUR_RADIUS = "40px"
    }
}