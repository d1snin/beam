/*
 * Copyright 2023-2024 Mikhail Titov
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
import dev.d1s.beam.commons.contententity.EmbedContentEntityTypeDefinition
import dev.d1s.beam.commons.contententity.get
import dev.d1s.beam.ui.util.justifyContent
import dev.d1s.beam.ui.util.renderSpinnerOnLoading
import dev.d1s.exkt.kvision.bootstrap.dFlex
import dev.d1s.exkt.kvision.bootstrap.overflowHidden
import dev.d1s.exkt.kvision.bootstrap.rounded
import dev.d1s.exkt.kvision.bootstrap.w100
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

    override fun SimplePanel.render(context: SingleContentEntityRenderingContext) {
        div {
            dFlex()
            w100()

            justifyContent(context.alignment)

            renderIframe(context.entity)

            separateContentEntity(context)
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

        div {
            rounded()
            overflowHidden()

            this.width = calculatedWidth

            height?.let {
                this.height = it.px
            }

            renderSpinnerOnLoading(resourceUrl = url) {
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
}