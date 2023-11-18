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

package dev.d1s.beam.ui.util

import dev.d1s.beam.commons.contententity.Alignment
import io.kvision.panel.SimplePanel

fun SimplePanel.justifyContent(alignment: Alignment) {
    val className = when (alignment) {
        Alignment.START -> "justify-content-start"
        Alignment.CENTER -> "justify-content-center"
        Alignment.END -> "justify-content-end"
    }

    addCssClass(className)
}

fun SimplePanel.alignItems(alignment: Alignment) {
    val className = when (alignment) {
        Alignment.START -> "align-items-start"
        Alignment.CENTER -> "align-items-center"
        Alignment.END -> "align-items-end"
    }

    addCssClass(className)
}

fun SimplePanel.alignText(alignment: Alignment) {
    val className = when (alignment) {
        Alignment.START -> "text-start"
        Alignment.CENTER -> "text-center"
        Alignment.END -> "text-end"
    }

    addCssClass(className)
}