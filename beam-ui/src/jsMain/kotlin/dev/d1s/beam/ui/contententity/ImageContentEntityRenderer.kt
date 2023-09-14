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

import dev.d1s.beam.commons.Block
import dev.d1s.beam.commons.contententity.ContentEntities
import dev.d1s.beam.commons.contententity.ContentEntity
import dev.d1s.beam.commons.contententity.ImageContentEntityTypeDefinition
import dev.d1s.beam.commons.contententity.get
import io.kvision.html.div
import io.kvision.html.image
import io.kvision.panel.SimplePanel
import io.kvision.utils.perc
import io.kvision.utils.px
import org.koin.core.component.KoinComponent

class ImageContentEntityRenderer : ContentEntityRenderer, KoinComponent {

    override val definition = ImageContentEntityTypeDefinition

    override fun SimplePanel.render(sequence: ContentEntities, block: Block) {
        renderImageRow {
            renderImages(sequence)
        }
    }

    private fun SimplePanel.renderImageRow(block: SimplePanel.() -> Unit) {
        div(className = "row row-cols-auto g-3") {
            block()
        }
    }

    private fun SimplePanel.renderImages(sequence: ContentEntities) {
        sequence.forEach { entity ->
            renderImage(entity)
        }
    }

    private fun SimplePanel.renderImage(entity: ContentEntity) {
        val parameters = entity.parameters

        val src = parameters[definition.url]
        requireNotNull(src)

        val description = parameters[definition.description]

        val width = parameters[definition.width]?.toInt()
        val height = parameters[definition.height]?.toInt()

        container {
            image(src, description, responsive = true, className = "rounded") {
                width?.let {
                    this.width = it.perc
                }

                height?.let {
                    this.height = it.px
                }
            }
        }
    }

    private fun SimplePanel.container(block: SimplePanel.() -> Unit) {
        div(className = "w-100") {
            block()
        }
    }
}