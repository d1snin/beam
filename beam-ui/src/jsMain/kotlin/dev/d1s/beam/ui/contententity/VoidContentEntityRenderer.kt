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

package dev.d1s.beam.ui.contententity

import dev.d1s.beam.commons.contententity.ContentEntity
import dev.d1s.beam.commons.contententity.VoidContentEntityTypeDefinition
import dev.d1s.beam.commons.contententity.get
import dev.d1s.beam.ui.util.Size
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import io.kvision.utils.px
import org.koin.core.component.KoinComponent

class VoidContentEntityRenderer : SingleContentEntityRenderer, KoinComponent {

    override val definition = VoidContentEntityTypeDefinition

    private val defaultHeight = Size.Sm

    override fun SimplePanel.render(entity: ContentEntity) {
        val height = entity.parameters[definition.height]?.toIntOrNull() ?: defaultHeight

        div(className = "w-100") {
            this.height = height.px
        }
    }
}