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

import dev.d1s.beam.commons.BlockSize
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.state.bindToMaxBlockSize
import dev.d1s.beam.ui.theme.setOutline
import dev.d1s.beam.ui.theme.setOverlay
import dev.d1s.beam.ui.util.Size.sizeOf
import dev.d1s.beam.ui.util.Texts
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.render
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import io.kvision.utils.px
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ExploreDropdownComponent : Component<Unit>(), KoinComponent {

    private val spaceListingComponent by inject<Component<Unit>>(Qualifier.SpaceListingComponent)

    override fun SimplePanel.render() {
        div(className = "d-flex") {
            div(className = "dropdown") {
                button(
                    Texts.Heading.ExploreDropdown.CALLOUT,
                    style = ButtonStyle.OUTLINELIGHT,
                    className = "btn-sm dropdown-toggle"
                ) {
                    setAttribute("data-bs-toggle", "dropdown")
                    setAttribute("data-bs-auto-close", "outside")
                    setAttribute("aria-expanded", "false")
                }

                div(className = "dropdown-menu p-3") {
                    bindToMaxBlockSize { size ->
                        val sizes = BlockSize.entries
                        val previousSize = sizes.getOrNull(sizes.indexOf(size) - 1)

                        previousSize?.let {
                            width = sizeOf(it).px
                        }
                    }

                    setOutline()
                    setOverlay()

                    render(spaceListingComponent)
                }
            }
        }
    }
}