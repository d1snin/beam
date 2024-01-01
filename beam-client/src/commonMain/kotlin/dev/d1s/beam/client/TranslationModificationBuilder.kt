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

package dev.d1s.beam.client

import dev.d1s.beam.commons.*

@BuilderDsl
public class TranslationModificationBuilder {

    public var languageCode: LanguageCode? = null

    public var languageName: LanguageName? = null

    public var default: Boolean? = null

    private var translations: TranslationMap = mapOf()

    public fun translations(vararg pairs: Pair<TextLocation, TranslatedText>) {
        translations = mapOf(*pairs)
    }

    public fun buildTranslationModification(): TranslationModification =
        TranslationModification(
            languageCode ?: error("Translation language code is undefined"),
            languageName,
            default ?: error("Translation default status is undefined"),
            translations
        )
}