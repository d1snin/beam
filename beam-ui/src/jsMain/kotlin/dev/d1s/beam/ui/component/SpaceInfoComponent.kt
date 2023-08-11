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

import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.state.CurrentSpaceChange
import dev.d1s.beam.ui.state.Observable
import dev.d1s.beam.ui.state.bindToCurrentTheme
import dev.d1s.beam.ui.theme.currentTheme
import dev.d1s.beam.ui.util.Texts
import dev.d1s.beam.ui.util.currentSpaceLink
import dev.d1s.exkt.kvision.component.Component
import io.kvision.core.JustifyContent
import io.kvision.html.p
import io.kvision.html.span
import io.kvision.panel.SimplePanel
import io.kvision.panel.vPanel
import io.kvision.state.bind
import io.kvision.utils.rem
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SpaceInfoComponent : Component<Unit>(), KoinComponent {

    private val currentSpaceChangeObservable by inject<Observable<CurrentSpaceChange>>(Qualifier.CurrentSpaceChangeObservable)

    override fun SimplePanel.render() {
        currentSpaceLink {
            vPanel(justify = JustifyContent.CENTER, className = "ms-2 ms-lg-3") {
                bind(currentSpaceChangeObservable.state) {
                    val space = it.space

                    p(className = "h2 mb-0") {
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
            }
        }
    }
}