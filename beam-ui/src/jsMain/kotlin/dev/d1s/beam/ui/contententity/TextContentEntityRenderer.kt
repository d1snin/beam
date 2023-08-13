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
import dev.d1s.beam.ui.state.bindToCurrentTheme
import dev.d1s.beam.ui.theme.currentTheme
import io.kvision.html.*
import io.kvision.panel.SimplePanel
import io.kvision.utils.rem
import org.koin.core.component.KoinComponent

class TextContentEntityRenderer : ContentEntityRenderer, KoinComponent {

    override val definition = TextContentEntityTypeDefinition

    override fun SimplePanel.render(sequence: ContentEntities) {
        p(className = "mb-0") {
            sequence.forEach { entity ->
                val isFirst = entity == sequence.first()
                val isLast = entity == sequence.last()

                renderTextEntity(entity.parameters, isFirst, isLast)
            }
        }
    }

    private fun SimplePanel.renderTextEntity(parameters: ContentEntityParameters, first: Boolean, last: Boolean) {
        optionalLink(parameters) {
            optionalCodeBlock(parameters) {
                optionalHeading(parameters, first, last) {
                    optionalParagraph(parameters, last) {
                        val content = parameters[definition.value]
                        requireNotNull(content)

                        val classList = buildClasses(parameters)

                        span(content, className = classList) {
                            optionalSecondaryText(parameters)
                        }
                    }
                }
            }
        }
    }

    private inline fun SimplePanel.optionalLink(
        parameters: ContentEntityParameters,
        crossinline block: SimplePanel.() -> Unit
    ) {
        parameters[definition.url]?.let { url ->
            link("", url) {
                block()
            }
        } ?: block()
    }

    private inline fun SimplePanel.optionalCodeBlock(
        parameters: ContentEntityParameters,
        crossinline block: SimplePanel.() -> Unit
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

    private inline fun SimplePanel.optionalHeading(
        parameters: ContentEntityParameters,
        first: Boolean,
        last: Boolean,
        crossinline block: SimplePanel.() -> Unit
    ) {
        val heading = parameters[definition.heading]?.let {
            TextContentEntityTypeDefinition.Heading.byKey(it)
        }

        heading?.let {
            val className = it.toBootstrapHeadingClass()

            p(className = className) {
                if (!first) {
                    addCssClass("mt-3")
                }

                if (!last) {
                    addCssClass("mb-2")
                } else {
                    addCssClass("mb-0")
                }

                block()
            }
        } ?: block()
    }

    private fun TextContentEntityTypeDefinition.Heading.toBootstrapHeadingClass() =
        when (this) {
            TextContentEntityTypeDefinition.Heading.H1 -> "h1"
            TextContentEntityTypeDefinition.Heading.H2 -> "h3"
            TextContentEntityTypeDefinition.Heading.H3 -> "h5"
        }

    private inline fun SimplePanel.optionalParagraph(
        parameters: ContentEntityParameters,
        last: Boolean,
        crossinline block: SimplePanel.() -> Unit
    ) {
        val paragraph = parameters.getBoolean(definition.paragraph)

        if (paragraph) {
            p {
                if (last) {
                    addCssClass("mb-0")
                }

                block()
            }
        } else {
            block()
        }
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

            bindToCurrentTheme {
                color = currentTheme.secondaryText
            }
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