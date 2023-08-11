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

package dev.d1s.beam.ui.theme

import dev.d1s.beam.commons.SpaceThemeDefinition
import dev.d1s.beam.commons.Url
import io.kvision.core.Background
import io.kvision.core.Color
import io.kvision.core.Widget

abstract class AbstractTheme(val definition: SpaceThemeDefinition) {

    abstract val normalSpaceIcon: Url

    abstract val notFoundIcon: Url

    abstract val emptySpaceIcon: Url

    abstract val buttonClass: String

    abstract val background: Color

    abstract val overlay: Color

    abstract val outline: Color

    abstract val text: Color

    abstract val secondaryText: Color

    abstract val green: Color

    abstract val orange: Color

    abstract val red: Color
}

fun Widget.setTextColor() {
    color = currentTheme.text
}

fun Widget.setBackground() {
    background = Background(color = currentTheme.background)
}