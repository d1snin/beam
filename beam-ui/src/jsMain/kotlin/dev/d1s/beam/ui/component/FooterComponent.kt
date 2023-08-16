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

import dev.d1s.beam.ui.state.bindToCurrentTheme
import dev.d1s.beam.ui.theme.currentTheme
import dev.d1s.beam.ui.util.Texts
import dev.d1s.exkt.kvision.component.Component
import io.kvision.html.div
import io.kvision.html.link
import io.kvision.html.span
import io.kvision.panel.SimplePanel
import io.kvision.utils.rem
import org.koin.core.component.KoinComponent

class FooterComponent : Component<Unit>(), KoinComponent {

    override fun SimplePanel.render() {
        div(className = "container-fluid pb-2 mt-auto d-flex flex-column align-items-start") {
            fontSize = 0.85.rem

            bindToCurrentTheme {
                color = currentTheme.secondaryText
            }

            div {
                span(Texts.Footer.TEXT_PART_1)
                nbsp()
                span(Texts.Footer.TEXT_PART_2) {
                    bindToCurrentTheme {
                        color = currentTheme.secondaryBlue
                    }
                }
                nbsp()
                span(Texts.Footer.TEXT_PART_3)
            }

            div {
                link(Texts.Footer.SOURCE_CODE_REFERENCE, Texts.Footer.SOURCE_CODE_LINK) {
                    bindToCurrentTheme {
                        color = currentTheme.secondaryText
                    }
                }
            }
        }
    }

    private fun SimplePanel.nbsp() =
        span {
            addAfterInsertHook {
                getElement()?.innerHTML = "&nbsp;"
            }
        }
}