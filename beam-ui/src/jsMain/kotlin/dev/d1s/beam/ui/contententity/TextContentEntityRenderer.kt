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

import dev.d1s.beam.commons.contententity.*
import dev.d1s.beam.ui.util.alignText
import io.kvision.html.p
import io.kvision.panel.SimplePanel
import org.koin.core.component.KoinComponent

class TextContentEntityRenderer : ContentEntityRenderer, KoinComponent {

    override val definition = TextContentEntityTypeDefinition

    override fun SimplePanel.render(context: SequenceContentEntityRenderingContext) {
        p {
            renderSequence(context)
            separateContentEntities(context)
        }
    }

    private fun SimplePanel.renderSequence(context: SequenceContentEntityRenderingContext) {
        context.sequence.forEach { entity ->
            renderTextEntity(entity.parameters, entity, context)
        }
    }

    private fun SimplePanel.renderTextEntity(
        parameters: ContentEntityParameters,
        contentEntity: ContentEntity,
        context: SequenceContentEntityRenderingContext
    ) {
        optionalHeading(parameters, contentEntity, context) {
            paragraph {
                alignText(context.alignment)

                renderText(parameters)
            }
        }
    }

    private fun SimplePanel.optionalHeading(
        parameters: ContentEntityParameters,
        contentEntity: ContentEntity,
        context: SequenceContentEntityRenderingContext,
        init: SimplePanel.() -> Unit
    ) {
        val heading = parameters[definition.heading]?.let {
            Heading.byName(it)
        }

        heading?.let {
            val className = it.toBootstrapHeadingClass()

            p(className = className) {
                init()
                separateContentEntity(contentEntity, context, topMargin = 4, bottomMargin = 2)
            }
        } ?: init()
    }

    private fun SimplePanel.paragraph(block: SimplePanel.() -> Unit) {
        p(className = "mb-0") {
            block()
        }
    }

    private fun SimplePanel.renderText(parameters: ContentEntityParameters) {
        val content = parameters[definition.value]
        requireNotNull(content)

        renderStyledText(content)
    }

    private fun Heading.toBootstrapHeadingClass() =
        when (this) {
            Heading.H1 -> "h1"
            Heading.H2 -> "h3"
            Heading.H3 -> "h5"
            Heading.DISPLAY_1 -> "display-1"
            Heading.DISPLAY_2 -> "display-3"
            Heading.DISPLAY_3 -> "display-5"
        }
}