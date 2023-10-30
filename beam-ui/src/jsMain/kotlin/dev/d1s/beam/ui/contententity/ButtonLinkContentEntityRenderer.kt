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

import dev.d1s.beam.commons.contententity.ButtonLinkContentEntityTypeDefinition
import dev.d1s.beam.commons.contententity.ContentEntities
import dev.d1s.beam.commons.contententity.ContentEntity
import dev.d1s.beam.commons.contententity.get
import dev.d1s.beam.ui.util.iconWithMargin
import dev.d1s.beam.ui.util.justifyContent
import dev.d1s.beam.ui.util.renderFriendlyLink
import io.kvision.html.div
import io.kvision.html.span
import io.kvision.panel.SimplePanel
import io.kvision.utils.perc
import io.kvision.utils.px
import org.koin.core.component.KoinComponent

class ButtonLinkContentEntityRenderer : ContentEntityRenderer, KoinComponent {

    override val definition = ButtonLinkContentEntityTypeDefinition

    private val ContentEntity.width get() = parameters[definition.width]

    override fun SimplePanel.render(context: SequenceContentEntityRenderingContext) {
        context.sequence.splitBy(selector = { it.width != null }) { entities, sized ->
            renderButtons(entities, context, sized)
        }
    }

    private fun SimplePanel.renderButtons(
        entities: ContentEntities,
        context: SequenceContentEntityRenderingContext,
        sized: Boolean
    ) {
        fun SimplePanel.renderEach(fullWidth: Boolean) {
            entities.forEach { entity ->
                renderButton(entity, context, fullWidth)
            }
        }

        if (sized) {
            renderEach(fullWidth = true)
        } else {
            div(className = "row row-cols-auto g-2") {
                justifyContent(context.alignment)

                renderEach(fullWidth = false)

                separateContentEntities(entities, context)
            }
        }
    }

    private fun SimplePanel.renderButton(
        entity: ContentEntity,
        context: SequenceContentEntityRenderingContext,
        fullWidth: Boolean
    ) {
        val parameters = entity.parameters

        val text = parameters[definition.text]
        requireNotNull(text)

        val icon = parameters[definition.icon]

        val url = parameters[definition.url]
        requireNotNull(url)

        val styleIdentifier = parameters[definition.style]
        val style = ButtonLinkContentEntityTypeDefinition.Style.byName(styleIdentifier)
            ?: ButtonLinkContentEntityTypeDefinition.Style.Default

        val width = parameters[definition.width]?.toInt()
        val height = parameters[definition.height]?.toInt()

        fun SimplePanel.render() {
            div(className = "d-flex mt-0") {
                width?.let {
                    this.width = width.perc
                }

                height?.let {
                    this.height = height.px
                }

                renderFriendlyLink(
                    url = url,
                    className = "w-100 h-100 btn btn-outline-${style.code}",
                    external = true
                ) {
                    role = "button"

                    div(className = "h-100 d-flex justify-content-center align-items-center") {
                        icon?.let {
                            iconWithMargin("bi bi-$it")
                        }

                        span(text)
                    }
                }

                if (fullWidth) {
                    separateContentEntity(entity, context)
                }
            }
        }

        if (fullWidth) {
            div(className = "d-flex w-100") {
                justifyContent(context.alignment)

                render()
            }
        } else {
            render()
        }
    }
}