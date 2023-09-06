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
import dev.d1s.beam.commons.contententity.ContentEntity
import dev.d1s.beam.commons.contententity.EmbedContentEntityTypeDefinition
import dev.d1s.beam.commons.contententity.get
import io.kvision.core.Border
import io.kvision.core.BorderStyle
import io.kvision.core.Position
import io.kvision.html.div
import io.kvision.html.iframe
import io.kvision.panel.SimplePanel
import io.kvision.utils.perc
import io.kvision.utils.px
import org.koin.core.component.KoinComponent

class EmbedContentEntityRenderer : SingleContentEntityRenderer, KoinComponent {

    override val definition = EmbedContentEntityTypeDefinition

    override fun SimplePanel.render(entity: ContentEntity, block: Block) {
        div(className = "d-flex w-100") {
            renderIframe(entity)
            separateContentEntity(entity, block)
        }
    }

    private fun SimplePanel.renderIframe(entity: ContentEntity) {
        val parameters = entity.parameters

        val url = parameters[definition.url]
        requireNotNull(url)

        val document = parameters[definition.document]

        val width = parameters[definition.width]?.toInt()
        val height = parameters[definition.height]?.toInt()

        val calculatedWidth = (width ?: 100).perc

        div(className = "rounded overflow-hidden") {
            this.width = calculatedWidth

            height?.let {
                this.height = it.px
            }

            iframe(
                src = url
            ) {
                document?.let {
                    srcdoc = it
                }

                position = Position.RELATIVE

                this.width = calculatedWidth
                this.height = this@div.height

                border = Border(style = BorderStyle.NONE)

                setAttribute("allowfullscreen", "true")
            }
        }
    }
}