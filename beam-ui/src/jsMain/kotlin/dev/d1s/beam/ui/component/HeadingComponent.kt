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

import dev.d1s.beam.commons.spaceShowStatus
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.util.currentSpace
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import dev.d1s.exkt.kvision.component.render
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import io.kvision.utils.px
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

class HeadingComponent : Component<Unit>(), KoinComponent {

    private val exploreDropdownComponent by inject<Component<Unit>>(Qualifier.ExploreDropdownComponent)

    private val daemonStatusComponent by inject<Component<Unit>>(Qualifier.DaemonStatusComponent)

    override fun SimplePanel.render(): Effect {
        div(className = "container-fluid px-3 mt-3 mb-5 d-flex flex-column flex-lg-row justify-content-lg-between") {
            renderSpaceHeading()
            renderDaemonStatus()
        }

        return Effect.Success
    }

    private fun SimplePanel.renderSpaceHeading() {
        div(className = "d-flex align-items-center") {
            renderSpaceCard()
            renderExploreDropdown()
        }
    }

    private fun SimplePanel.renderSpaceCard() {
        val spaceCard = get<Component<SpaceCardComponent.Config>>(Qualifier.SpaceCardComponent)

        div(className = "me-2") {
            render(spaceCard) {
                bare.value = true
                cardPaddingLevel.value = 4
                iconWidth.value = 45.px
                enableHeading.value = true
            }
        }
    }

    private fun SimplePanel.renderExploreDropdown() {
        render(exploreDropdownComponent)
    }

    private fun SimplePanel.renderDaemonStatus() {
        div(className = "align-self-center mt-5 mt-lg-0") {
            visible = false

            val showStatus = currentSpace?.metadata.spaceShowStatus

            if (showStatus) {
                render(daemonStatusComponent)
                visible = true
            }
        }
    }
}