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

import kotlinx.serialization.Serializable

public typealias TranslationQualifier = String

public typealias Template = String

public typealias LanguageCode = String
public typealias LanguageName = String

public typealias TextLocation = String

public typealias TranslatedText = String

public typealias TranslationMap = Map<TextLocation, TranslatedText>

public typealias Translations = List<Translation>

public val TextLocation.asTemplate: Template get() = "\${$this}"

public fun TranslationQualifier(spaceId: SpaceId?, languageCode: LanguageCode): TranslationQualifier =
    (spaceId?.let { "$it-" } ?: "") + languageCode

public sealed interface AbstractTranslation {

    public val languageCode: LanguageCode

    public val languageName: LanguageName?

    public val default: Boolean

    public val translations: TranslationMap
}

@Serializable
public data class Translation(
    val space: SpaceId?,
    override val languageCode: LanguageCode,
    override val languageName: LanguageName,
    override val default: Boolean,
    override val translations: TranslationMap
) : AbstractTranslation

@Serializable
public data class TranslationModification(
    override val languageCode: LanguageCode,
    override val languageName: LanguageName?,
    override val default: Boolean,
    override val translations: TranslationMap
) : AbstractTranslation