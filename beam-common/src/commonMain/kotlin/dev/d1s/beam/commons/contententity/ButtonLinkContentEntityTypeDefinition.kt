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

package dev.d1s.beam.commons.contententity

import dev.d1s.beam.commons.util.lowercaseName

public data object ButtonLinkContentEntityTypeDefinition : CommonContentEntityTypeDefinition(name = "button-link") {

    val text: ContentEntityParameterDefinition = parameter("text", required = true, translatable = true)

    val icon: ContentEntityParameterDefinition = parameter("icon")

    val url: ContentEntityParameterDefinition = urlParameter(required = true)

    val style: ContentEntityParameterDefinition = parameter("style")

    val width: ContentEntityParameterDefinition = widthParameter()

    val height: ContentEntityParameterDefinition = heightParameter()
}

public enum class ButtonStyle(public val code: String) {
    PRIMARY("primary"),
    SUCCESS("success"),
    DANGER("danger"),
    WARNING("warning"),
    INFO("info"),
    LIGHT("light");

    public companion object {

        public val Default: ButtonStyle = LIGHT

        public fun byName(name: String): ButtonStyle? =
            entries.find {
                it.lowercaseName == name
            }
    }
}