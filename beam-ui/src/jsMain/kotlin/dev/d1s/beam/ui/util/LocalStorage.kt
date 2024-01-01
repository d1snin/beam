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

package dev.d1s.beam.ui.util

import kotlinx.browser.localStorage

object LocalStorage {

    private const val LANGUAGE_CODE = "language_code"

    var languageCode
        get() = getItem(LANGUAGE_CODE)
        set(value) = setItem(LANGUAGE_CODE, value)

    private fun getItem(key: String) =
        localStorage.getItem(key)

    private fun setItem(key: String, value: String?) {
        value ?: return

        localStorage.setItem(key, value)
    }
}