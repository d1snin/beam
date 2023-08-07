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

import dev.d1s.beam.commons.Block
import dev.d1s.beam.ui.contententity.renderEntities
import dev.d1s.beam.ui.util.Size.sizeOf
import dev.d1s.exkt.kvision.component.Component
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.utils.px
import org.koin.core.component.KoinComponent

class BlockComponent : Component<BlockComponent.Config>(::Config), KoinComponent {

    override fun SimplePanel.render() {
        div().bind(config.block) { block ->
            block ?: error("Block isn't set")

            card("p-4 d-flex flex-column justify-content-start mb-4") {
                width = sizeOf(block.size).px
                renderEntities(block.entities)
            }
        }
    }

    class Config {

        val block = ObservableValue<Block?>(null)
    }
}