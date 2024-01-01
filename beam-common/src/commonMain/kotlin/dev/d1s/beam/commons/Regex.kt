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

import kotlin.text.Regex

public object Regex {

    public val Slug: Regex = Regex("([a-z0-9]{1,20})(-[a-z0-9]{0,20}){0,10}")

    public val Metadata: Regex = Regex("([a-z0-9]{1,20})([-.][a-z0-9]{0,20}){0,10}")

    public val Template: Regex = Regex("(?<!\\\\)\\\$\\{([a-z0-9]{1,20})([-.][a-z0-9]{0,20}){0,10}\\}")
    public val EscapedTemplate: Regex = Regex("\\\\\\\$\\{([a-z0-9]{1,20})([-.][a-z0-9]{0,20}){0,10}\\}")
    public val TemplateEscape: Regex = Regex("\\\\(?=\\\$\\{([a-z0-9]{1,20})([-.][a-z0-9]{0,20}){0,10}\\})")
    public val TextLocation: Regex = Regex("(?<!\\\\)(?<=\\\$\\{)([a-z0-9]{1,20})([-.][a-z0-9]{0,20}){0,10}(?=\\})")
    public val UnwrappedTextLocation: Regex = Regex("([a-z0-9]{1,20})([-.][a-z0-9]{0,20}){0,10}")
    public val LanguageCode: Regex = Regex("[a-z]{2}")
}