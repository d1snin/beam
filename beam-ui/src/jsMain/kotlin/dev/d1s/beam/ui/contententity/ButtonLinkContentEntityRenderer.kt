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
import dev.d1s.beam.commons.contententity.ButtonLinkContentEntityTypeDefinition
import dev.d1s.beam.commons.contententity.ContentEntities
import dev.d1s.beam.commons.contententity.ContentEntity
import dev.d1s.beam.commons.contententity.get
import io.kvision.html.div
import io.kvision.html.link
import io.kvision.panel.SimplePanel
import io.kvision.utils.perc
import io.kvision.utils.px
import org.koin.core.component.KoinComponent

class ButtonLinkContentEntityRenderer : ContentEntityRenderer, KoinComponent {

    override val definition = ButtonLinkContentEntityTypeDefinition

    private val ContentEntity.width get() = parameters[definition.width]

    override fun SimplePanel.render(sequence: ContentEntities, block: Block) {
        sequence.split(condition = { it.width != null }) { entities, sized ->
            renderButtons(entities, block, sized)
        }
    }

    private fun SimplePanel.renderButtons(entities: ContentEntities, block: Block, sized: Boolean) {
        fun SimplePanel.renderEach(fullWidth: Boolean) {
            entities.forEach { entity ->
                renderButton(entity, block, fullWidth)
            }
        }

        if (sized) {
            renderEach(fullWidth = true)
        } else {
            div(className = "row row-cols-auto g-2") {
                renderEach(fullWidth = false)

                separateContentEntities(entities, block)
            }
        }
    }

    private fun SimplePanel.renderButton(entity: ContentEntity, block: Block, fullWidth: Boolean) {
        val parameters = entity.parameters

        val text = parameters[definition.text]
        requireNotNull(text)

        val url = parameters[definition.url]
        requireNotNull(url)

        val styleIdentifier = parameters[definition.style]
        val style = ButtonLinkContentEntityTypeDefinition.Style.byIdentifier(styleIdentifier)
            ?: ButtonLinkContentEntityTypeDefinition.Style.Default

        val width = parameters[definition.width]?.toInt()
        val height = parameters[definition.height]?.toInt()

        fun render() {
            div(className = "d-flex") {
                width?.let {
                    this.width = width.perc
                }

                height?.let {
                    this.height = height.px
                }

                link(text, url, className = "w-100 h-100 btn btn-outline-${style.identifier}") {
                    role = "button"
                }

                if (fullWidth) {
                    separateContentEntity(entity, block)
                }
            }
        }

        if (fullWidth) {
            div(className = "d-flex w-100") {
                render()
            }
        } else {
            render()
        }
    }
}