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

package dev.d1s.beam.ui.component

import io.kvision.core.CssSize
import io.kvision.html.p
import io.kvision.panel.SimplePanel
import io.kvision.utils.rem

interface SpaceSearchCardContent {

    val mode: SpaceSearchCardComponent.Mode

    fun SimplePanel.configureContainer() {
        // nop
    }

    fun SimplePanel.image()

    fun SimplePanel.text()
}

fun SimplePanel.spaceSearchCardText(text: String, fontSize: CssSize = 1.6.rem) {
    p(text, className = "mb-3") {
        this.fontSize = fontSize
    }
}