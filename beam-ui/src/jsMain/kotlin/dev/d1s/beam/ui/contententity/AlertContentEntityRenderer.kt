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

import dev.d1s.beam.commons.contententity.AlertContentEntityTypeDefinition
import dev.d1s.beam.commons.contententity.AlertStyle
import dev.d1s.beam.commons.contententity.get
import dev.d1s.beam.ui.util.iconWithMargin
import dev.d1s.beam.ui.util.justifyContent
import io.kvision.html.div
import io.kvision.html.span
import io.kvision.panel.SimplePanel
import io.kvision.utils.perc
import io.kvision.utils.px
import org.koin.core.component.KoinComponent

class AlertContentEntityRenderer : SingleContentEntityRenderer, KoinComponent {

    override val definition = AlertContentEntityTypeDefinition

    override fun SimplePanel.render(context: SingleContentEntityRenderingContext) {
        div(className = "d-flex w-100") {
            justifyContent(context.alignment)

            renderAlertCard(context)

            separateContentEntity(context)
        }
    }

    private fun SimplePanel.renderAlertCard(context: SingleContentEntityRenderingContext) {
        val parameters = context.entity.parameters

        val text = parameters[definition.text]
        requireNotNull(text)

        val icon = parameters[definition.icon]

        val style = parameters[definition.style]?.let { AlertStyle.byName(it) } ?: AlertStyle.Default

        val width = parameters[definition.width]?.toInt()
        val height = parameters[definition.height]?.toInt()

        val styleCode = style.code

        div(className = "card border-${styleCode} bg-transparent overflow-hidden") {
            width?.let {
                this.width = width.perc
            }

            height?.let {
                this.height = height.px
            }

            div(className = "card-body text-$styleCode") {
                div(className = "h-100 d-flex justify-content-start align-items-center") {
                    icon?.let {
                        iconWithMargin("bi bi-$it")
                    }

                    span(text)
                }
            }
        }
    }
}