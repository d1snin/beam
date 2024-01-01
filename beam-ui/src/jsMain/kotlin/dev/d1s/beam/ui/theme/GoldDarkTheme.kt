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

package dev.d1s.beam.ui.theme

import dev.d1s.beam.commons.SpaceThemeDefinition
import dev.d1s.beam.ui.resource.ResourceLocation
import dev.d1s.beam.ui.util.SpinnerStyle
import io.kvision.core.Color
import io.kvision.html.ButtonStyle

object GoldDarkTheme : AbstractTheme(SpaceThemeDefinition.GoldDark) {

    override val notFoundIcon = ResourceLocation.NOT_FOUND_LIGHT

    override val emptySpaceIcon = ResourceLocation.EMPTY_SPACE_LIGHT

    override val lostConnectionIcon = ResourceLocation.LOST_CONNECTION_LIGHT

    override val buttonStyle = ButtonStyle.OUTLINELIGHT

    override val spinnerStyle = SpinnerStyle.LIGHT

    override val shadow = true

    // https://colorpicker.me/#151515
    override val background = Color.hex(0x151515)

    // https://colorpicker.me/#242424
    override val overlay = Color.hex(0x242424)

    // https://colorpicker.me/#aba54a
    override val outline = Color.hex(0xaba54a)

    // https://colorpicker.me/#f0f0f0
    override val text = Color.hex(0xf0f0f0)

    // https://colorpicker.me/#959595
    override val secondaryText = Color.hex(0x959595)

    // https://colorpicker.me/#70adc7
    override val secondaryBlue = Color.hex(0x70adc7)

    // https://colorpicker.me/#ff7878
    override val red = Color.hex(0xff7878)

    // https://colorpicker.me/#ffbe5e
    override val orange = Color.hex(0xffbe5e)

    // https://colorpicker.me/#89ffa9
    override val green = Color.hex(0x89ffa9)

    // https://colorpicker.me/#feff9e
    override val yellow = Color.hex(0xfeff9e)

    // https://colorpicker.me/#4dd3ff
    override val blue = Color.hex(0x4dd3ff)
}