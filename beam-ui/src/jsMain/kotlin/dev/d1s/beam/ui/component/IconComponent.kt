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

import dev.d1s.beam.ui.resource.ResourceLocation
import dev.d1s.beam.ui.util.Texts
import dev.d1s.beam.ui.util.currentSpace
import dev.d1s.exkt.kvision.component.Component
import io.kvision.html.div
import io.kvision.html.image
import io.kvision.panel.SimplePanel
import io.kvision.utils.px
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class IconComponent : Component<Unit>(), KoinComponent {

    private val renderingScope = CoroutineScope(Dispatchers.Main)

    override fun SimplePanel.render() {
        div {
            renderingScope.launch {
                val space = currentSpace()

                image(space?.view?.icon ?: ResourceLocation.ICON, alt = Texts.Heading.Logo.ALT) {
                    width = 45.px
                }
            }
        }
    }
}