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
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.render
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HeadingComponent : Component<Unit>(), KoinComponent {

    private val iconComponent by inject<Component<Unit>>(Qualifier.IconComponent)

    private val spaceInfoComponent by inject<Component<Unit>>(Qualifier.SpaceInfoComponent)

    private val daemonStatusComponent by inject<Component<Unit>>(Qualifier.DaemonStatusComponent)

    override fun SimplePanel.render() {
        div(className = "container-fluid mt-3 mb-4 my-5 d-flex flex-column flex-lg-row justify-content-lg-between") {
            div(className = "d-flex align-items-center") {
                render(iconComponent)
                render(spaceInfoComponent)
            }

            div(className = "align-self-center mt-4 mt-lg-0") {
                render(daemonStatusComponent)
            }
        }
    }
}