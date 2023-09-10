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

import com.catppuccin.kotlin.Palette
import com.catppuccin.kotlin.color.*
import dev.d1s.beam.commons.SpaceThemeDefinition
import io.kvision.core.Color

abstract class CatppuccinTheme(definition: SpaceThemeDefinition, palette: Palette) : AbstractTheme(definition) {

    override val background = palette.base.color

    override val overlay = palette.surface0.color

    override val outline = palette.overlay0.color

    override val text = palette.text.color

    override val secondaryText = palette.subtext0.color

    override val secondaryBlue = palette.blue.color

    override val red = palette.red.color

    override val orange = palette.peach.color

    override val green = palette.green.color

    override val blue = palette.blue.color

    private val PaletteColor.color
        get() = Color.hex(hex.intValue)
}