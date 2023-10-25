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

import dev.d1s.beam.commons.contententity.ImageContentEntityTypeDefinition
import dev.d1s.beam.commons.contententity.get
import dev.d1s.beam.ui.util.isFluidImage
import io.kvision.html.div
import io.kvision.html.image
import io.kvision.panel.SimplePanel
import io.kvision.utils.perc
import io.kvision.utils.px
import org.koin.core.component.KoinComponent

class ImageContentEntityRenderer : SingleContentEntityRenderer, KoinComponent {

    override val definition = ImageContentEntityTypeDefinition

    override fun SimplePanel.render(context: SingleContentEntityRenderingContext) {
        val parameters = context.entity.parameters

        val src = parameters[definition.url]
        requireNotNull(src)

        val description = parameters[definition.description]

        val width = parameters[definition.width]?.toInt()
        val height = parameters[definition.height]?.toInt()

        container(context) {
            if (!context.block.isFluidImage()) {
                separateContentEntity(context)
            }

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

    private fun SimplePanel.container(
        context: SingleContentEntityRenderingContext,
        configure: SimplePanel.() -> Unit
    ) {
        div(className = "w-100") {
            if (context.isFirst()) {
                addCssClass("mt-0")
            }

            configure()
        }
    }
}