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

import com.catppuccin.kotlin.color.*
import com.catppuccin.kotlin.mocha
import dev.d1s.beam.commons.SpaceThemeDefinition
import dev.d1s.beam.ui.resource.ResourceLocation
import io.kvision.core.Color
import io.kvision.html.ButtonStyle

object CatppuccinMochaTheme : AbstractTheme(SpaceThemeDefinition.CatppuccinMocha) {

    override val notFoundIcon = ResourceLocation.NOT_FOUND_LIGHT

    override val emptySpaceIcon = ResourceLocation.EMPTY_SPACE_LIGHT

    override val buttonStyle = ButtonStyle.OUTLINELIGHT

    override val background = mocha.base.color

    override val overlay = mocha.surface0.color

    override val outline = mocha.overlay0.color

    override val text = mocha.text.color

    override val secondaryText = mocha.subtext0.color

    override val secondaryBlue = mocha.blue.color

    override val green = mocha.green.color

    override val orange = mocha.peach.color

    override val red = mocha.red.color

    private val PaletteColor.color
        get() = Color.hex(hex.intValue)
}