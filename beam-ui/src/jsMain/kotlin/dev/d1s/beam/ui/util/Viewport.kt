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

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import kotlin.math.max

val vh get() = max(document.documentElement?.clientHeight ?: 0, window.innerHeight)
val vw get() = max(document.documentElement?.clientWidth ?: 0, window.innerWidth)

val Element.isOnScreen
    get() = getBoundingClientRect().let {
        it.top >= 0 && it.left >= 0 && it.bottom <= vh && it.right <= vw
    }

fun onScrollOrResize(block: () -> Unit) {
    window.addEventListener(Events.SCROLL, { _ -> block() })
    window.addEventListener(Events.RESIZE, { _ -> block() })
}