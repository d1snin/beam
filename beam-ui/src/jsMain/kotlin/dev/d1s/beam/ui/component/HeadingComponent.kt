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

    private val logoComponent by inject<Component<Unit>>(Qualifier.LogoComponent)

    private val daemonStatusComponent by inject<Component<Unit>>(Qualifier.DaemonStatusComponent)

    override fun SimplePanel.render() {
        div(className = "container-fluid mt-3 mb-4 my-5 d-flex justify-content-between align-items-center") {
            render(logoComponent)
            render(daemonStatusComponent)
        }
    }
}