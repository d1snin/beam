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

package dev.d1s.beam.commons

public val TextVariables: List<TextVariable> = listOf(
    VersionTextVariable
)

public sealed class TextVariable {

    public abstract val variable: String

    public abstract val replacement: String

    override fun toString(): String =
        "<$variable>"
}

public data object VersionTextVariable : TextVariable() {

    override val variable: String = "version"

    override val replacement: Version = VERSION
}

public fun String.processTextVariables(): String =
    replace(Regex.TextVariable) { res ->
        val value = res.value.unwrap()

        val variable = TextVariables.find {
            it.variable == value
        }

        variable?.replacement ?: res.value
    }

public fun TranslationMap.getWithTextVarProcessing(location: String): String? =
    get(location)?.processTextVariables()

private fun String.unwrap() = removePrefix("<").removeSuffix(">")