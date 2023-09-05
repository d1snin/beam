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
import dev.d1s.beam.commons.contententity.*
import dev.d1s.beam.ui.theme.setSecondaryText
import io.kvision.html.*
import io.kvision.panel.SimplePanel
import io.kvision.utils.rem
import org.koin.core.component.KoinComponent

class TextContentEntityRenderer : ContentEntityRenderer, KoinComponent {

    override val definition = TextContentEntityTypeDefinition

    override fun SimplePanel.render(sequence: ContentEntities, block: Block) {
        p {
            renderSequence(sequence, block)
            separateContentEntities(sequence, block)
        }
    }

    private fun SimplePanel.renderSequence(sequence: ContentEntities, block: Block) {
        sequence.forEach { entity ->
            renderTextEntity(entity.parameters, entity, block)
        }
    }

    private fun SimplePanel.renderTextEntity(
        parameters: ContentEntityParameters,
        contentEntity: ContentEntity,
        block: Block
    ) {
        optionalLink(parameters) {
            optionalCodeBlock(parameters) {
                optionalHeading(parameters, contentEntity, block) {
                    optionalParagraph(parameters) {
                        renderText(parameters)
                    }
                }
            }
        }
    }

    private fun SimplePanel.optionalLink(
        parameters: ContentEntityParameters,
        block: SimplePanel.() -> Unit
    ) {
        parameters[definition.url]?.let { url ->
            link("", url) {
                block()
            }
        } ?: block()
    }

    private fun SimplePanel.optionalCodeBlock(
        parameters: ContentEntityParameters,
        block: SimplePanel.() -> Unit
    ) {
        val monospace = parameters.getBoolean(definition.monospace)

        if (monospace) {
            tag(TAG.PRE) {
                code {
                    block()
                }
            }
        } else {
            block()
        }
    }

    private fun SimplePanel.optionalHeading(
        parameters: ContentEntityParameters,
        contentEntity: ContentEntity,
        block: Block,
        init: SimplePanel.() -> Unit
    ) {
        val heading = parameters[definition.heading]?.let {
            TextContentEntityTypeDefinition.Heading.byKey(it)
        }

        heading?.let {
            val className = it.toBootstrapHeadingClass()

            p(className = className) {
                init()
                separateContentEntity(contentEntity, block, topMargin = 3, bottomMargin = 2)
            }
        } ?: init()
    }

    private fun SimplePanel.optionalParagraph(
        parameters: ContentEntityParameters,
        block: SimplePanel.() -> Unit
    ) {
        val paragraph = parameters.getBoolean(definition.paragraph)

        if (paragraph) {
            p("mb-0") {
                block()
            }
        } else {
            block()
        }
    }

    private fun SimplePanel.renderText(parameters: ContentEntityParameters) {
        val content = parameters[definition.value]
        requireNotNull(content)

        val classList = buildClasses(parameters)

        span(content, className = classList) {
            optionalSecondaryText(parameters)
        }
    }

    private fun TextContentEntityTypeDefinition.Heading.toBootstrapHeadingClass() =
        when (this) {
            TextContentEntityTypeDefinition.Heading.H1 -> "h1"
            TextContentEntityTypeDefinition.Heading.H2 -> "h3"
            TextContentEntityTypeDefinition.Heading.H3 -> "h5"
        }

    private fun buildClasses(parameters: ContentEntityParameters): String {
        val classNames = buildList {
            parameters.ifTrue(definition.bold) {
                add("fw-bold")
            }

            parameters.ifTrue(definition.italic) {
                add("fst-italic")
            }

            parameters.ifTrue(definition.underline) {
                add("text-decoration-underline")
            }

            parameters.ifTrue(definition.strikethrough) {
                add("text-decoration-line-through")
            }
        }

        return classNames.joinToString(" ")
    }

    private fun SimplePanel.optionalSecondaryText(parameters: ContentEntityParameters) {
        parameters.ifTrue(definition.secondary) {
            fontSize = 0.9.rem

            setSecondaryText()
        }
    }

    private inline fun ContentEntityParameters.ifTrue(
        booleanParameter: ContentEntityParameterDefinition,
        block: () -> Unit
    ) {
        val parameterValue = this[booleanParameter] ?: return
        val parameter = parameterValue.toBooleanStrict()

        if (parameter) {
            block()
        }
    }

    private fun ContentEntityParameters.getBoolean(booleanParameter: ContentEntityParameterDefinition): Boolean {
        val parameterValue = this[booleanParameter] ?: return false
        return parameterValue.toBooleanStrict()
    }
}