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
import dev.d1s.beam.ui.state.bindToMaxBlockSize
import io.kvision.core.*
import io.kvision.utils.px
import org.w3c.dom.css.CSSStyleDeclaration

abstract class AbstractTheme(val definition: SpaceThemeDefinition) {

    abstract val notFoundIcon: Url

    abstract val emptySpaceIcon: Url

    abstract val buttonClass: String

    abstract val background: Color

    abstract val overlay: Color

    abstract val outline: Color

    abstract val text: Color

    abstract val secondaryText: Color

    abstract val secondaryBlue: Color

    abstract val green: Color

    abstract val orange: Color

    abstract val red: Color
}

fun StyledComponent.setBackground() {
    bindToMaxBlockSize {
        background = Background(color = currentTheme.background)
    }
}

fun CSSStyleDeclaration.setBackground() {
    bindToMaxBlockSize {
        backgroundColor = currentTheme.background.asString()
    }
}

fun StyledComponent.setOverlay() {
    bindToMaxBlockSize {
        background = Background(color = currentTheme.overlay)
    }
}

fun StyledComponent.setOutline() {
    bindToMaxBlockSize {
        outline = Outline(width = 1.px, style = OutlineStyle.SOLID, currentTheme.outline)
    }
}

fun StyledComponent.setTextColor() {
    bindToMaxBlockSize {
        color = currentTheme.text
    }
}

fun StyledComponent.setSecondaryText() {
    bindToMaxBlockSize {
        color = currentTheme.secondaryText
    }
}

fun StyledComponent.setSecondaryBlue() {
    bindToMaxBlockSize {
        color = currentTheme.secondaryBlue
    }
}