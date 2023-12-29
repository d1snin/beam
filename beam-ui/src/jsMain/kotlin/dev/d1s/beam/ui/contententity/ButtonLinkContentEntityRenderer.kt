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

import dev.d1s.beam.commons.blockButtonLinkEntityGrow
import dev.d1s.beam.commons.contententity.*
import dev.d1s.beam.ui.util.justifyContent
import dev.d1s.beam.ui.util.renderExternalButtonLink
import dev.d1s.beam.ui.util.renderRow
import dev.d1s.exkt.kvision.bootstrap.*
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
            renderRow(cols = "auto", gap = 2) {
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

        val style = parameters[definition.style]?.let { ButtonStyle.byName(it) } ?: ButtonStyle.Default

        val width = parameters[definition.width]?.toInt()
        val height = parameters[definition.height]?.toInt()

        fun SimplePanel.render() {
            div {
                dFlex()
                mt0()

                if (!fullWidth && context.block.metadata.blockButtonLinkEntityGrow) {
                    flexGrow1()
                }

                width?.let {
                    this.width = width.perc
                }

                height?.let {
                    this.height = height.px
                }

                renderExternalButtonLink(
                    url = url,
                    style = style
                ) {
                    w100()
                    h100()

                    div {
                        h100()
                        dFlex()
                        justifyContentCenter()
                        alignItemsCenter()

                        icon?.let {
                            bootstrapIconWithMargin(it)
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
            div {
                dFlex()
                w100()

                justifyContent(context.alignment)

                render()
            }
        } else {
            render()
        }
    }
}