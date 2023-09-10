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

import dev.d1s.beam.commons.Html
import dev.d1s.beam.ui.theme.currentTheme
import io.ktor.util.*
import io.kvision.core.Color

interface StyledTextRenderer {

    fun render(text: String): Html
}

class DefaultStyledTextRenderer : StyledTextRenderer {

    private val escapes = listOf(
        UrlEscape,
        ColorEscape,
        CodeBlockEscape,
        MonospaceEscape,
        BoldEscape,
        ItalicEscape,
        UnderlineEscape,
        StrikethroughEscape,
        SecondaryEscape,
        IconEscape
    )

    override fun render(text: String): Html =
        text.escapeHTML()
            .transformUrl()
            .transformColor()
            .transformCodeBlock()
            .transformMonospace()
            .transformBold()
            .transformItalic()
            .transformUnderline()
            .transformStrikethrough()
            .transformSecondary()
            .transformIcon()
            .removeEscapes()

    private fun String.transformUrl() =
        transformMatchedParametrizedStyle(Url) { (text, link) ->
            "<a href=\"$link\">$text</a>"
        }

    private fun String.transformColor() =
        transformMatchedParametrizedStyle(Color) { (text, colorDefinition) ->
            val textColor = TextColor.byDefinition(colorDefinition)

            textColor?.let {
                "<span style=\"color: ${textColor.color().asString()}\">$text</span>"
            } ?: text
        }

    private fun String.transformCodeBlock() =
        transformMatchedTextGroup(CodeBlock) { text ->
            "<pre><code>$text</code></pre>"
        }

    private fun String.transformMonospace() =
        transformMatchedTextGroup(Monospace) { text ->
            "<code>$text</code>"
        }

    private fun String.transformBold() =
        transformWithClass(Bold, "fw-bold")

    private fun String.transformItalic() =
        transformWithClass(Italic, "fst-italic")

    private fun String.transformUnderline() =
        transformWithClass(Underline, "text-decoration-underline")

    private fun String.transformStrikethrough() =
        transformWithClass(Strikethrough, "text-decoration-line-through")

    private fun String.transformSecondary() =
        transformMatchedTextGroup(Secondary) { text ->
            "<span style=\"font-size: 0.9rem; color: ${currentTheme.secondaryText.asString()}\">$text</span>"
        }

    private fun String.transformIcon() =
        transformMatchedTextGroup(Icon) { text ->
            "<i class=\"bi bi-$text\"></i>"
        }

    private fun String.transformWithClass(regex: Regex, className: String) =
        transformMatchedTextGroup(regex) { text ->
            "<span class=\"$className\">$text</span>"
        }

    private fun String.transformMatchedTextGroup(regex: Regex, transformer: (String) -> String) =
        transformRecursively(regex) {
            val text = it.groupValues[1]
            transformer(text)
        }

    private fun String.transformMatchedParametrizedStyle(
        regex: Regex,
        transformer: (Pair<String, String>) -> String
    ): String {
        fun MatchResult.textAndParameter() = groupValues[1] to groupValues[2]

        val transformedText = transformRecursively(regex) {
            val textAndParameter = it.textAndParameter()
            transformer(textAndParameter)
        }

        return transformedText
    }

    private fun String.transformRecursively(regex: Regex, transformer: (MatchResult) -> String): String {
        var currentResult = this
        var currentDeepestMatch: MatchResult?

        do {
            currentDeepestMatch = currentResult.getDeepestMatch(regex)

            currentDeepestMatch?.let {
                val transformedValue = transformer(it)

                currentResult = currentResult.replace(it.value, transformedValue)
            }
        } while (currentDeepestMatch != currentResult.getDeepestMatch(regex))

        return currentResult
    }

    private fun String.removeEscapes() =
        escapes.fold(this) { acc, regex ->
            acc.replace(regex, "")
        }

    private fun String.getDeepestMatch(regex: Regex): MatchResult? {
        fun String.deeperMatch() = regex.find(this, startIndex = 1)

        var currentMatch = regex.find(this)
        var deeperMatch: MatchResult?

        do {
            deeperMatch = currentMatch?.value?.deeperMatch()

            deeperMatch?.let {
                currentMatch = it
            }
        } while (deeperMatch != null)

        return currentMatch
    }


    private enum class TextColor(val definition: String, val color: () -> Color) {
        RED("red", { currentTheme.red }),
        ORANGE("orange", { currentTheme.orange }),
        GREEN("green", { currentTheme.green }),
        BLUE("blue", { currentTheme.blue });

        companion object {

            fun byDefinition(definition: String) =
                entries.find {
                    it.definition == definition
                }
        }
    }

    private companion object {

        private val Url = Regex("(?<!\\\\)\\[([\\s\\S]+)\\]\\[(.+)\\]")
        private val UrlEscape = Regex("\\\\(?=\\[([\\s\\S]+)\\]\\[(.+)\\])")

        private val Color = Regex("(?<!\\\\)\\(([\\s\\S]+)\\)\\((.+)\\)")
        private val ColorEscape = Regex("\\\\(?=\\(([\\s\\S]+)\\)\\((.+)\\))")

        private val Monospace = Regex("(?<!\\\\)`([\\s\\S]+)`")
        private val MonospaceEscape = Regex("\\\\(?=`([\\s\\S]+)`)")

        private val CodeBlock = Regex("(?<!\\\\)```([\\s\\S]+)```")
        private val CodeBlockEscape = Regex("\\\\(?=```([\\s\\S]+)```)")

        private val Bold = Regex("(?<!\\\\)\\*\\*([\\s\\S]+)\\*\\*")
        private val BoldEscape = Regex("\\\\(?=\\*\\*([\\s\\S]+)\\*\\*)")

        private val Italic = Regex("(?<!\\\\)\\*([\\s\\S]+)\\*")
        private val ItalicEscape = Regex("\\\\(?=\\*([\\s\\S]+)\\*)")

        private val Underline = Regex("(?<!\\\\)__([\\s\\S]+)__")
        private val UnderlineEscape = Regex("\\\\(?=__([\\s\\S]+)__)")

        private val Strikethrough = Regex("(?<!\\\\)~~([\\s\\S]+)~~")
        private val StrikethroughEscape = Regex("\\\\(?=~~([\\s\\S]+)~~)")

        private val Secondary = Regex("(?<!\\\\)%([\\s\\S]+)%")
        private val SecondaryEscape = Regex("\\\\(?=%([\\s\\S]+)%)")

        private val Icon = Regex("(?<!\\\\):([a-z-]+):")
        private val IconEscape = Regex("\\\\(?=:([a-z-]+):)")
    }
}