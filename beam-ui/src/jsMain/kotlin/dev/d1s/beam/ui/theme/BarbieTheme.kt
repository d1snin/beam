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
import dev.d1s.beam.ui.resource.ResourceLocation
import io.kvision.core.Color
import io.kvision.html.ButtonStyle

object BarbieTheme : AbstractTheme(SpaceThemeDefinition.Barbie) {

    override val notFoundIcon = ResourceLocation.NOT_FOUND_DARK

    override val emptySpaceIcon = ResourceLocation.EMPTY_SPACE_DARK

    override val buttonStyle = ButtonStyle.OUTLINEDARK

    // https://colorpicker.me/#facde5
    override val background = Color.hex(0xfacde5)

    // https://colorpicker.me/#f7b9d7
    override val overlay = Color.hex(0xf7b9d7)

    // https://colorpicker.me/#ed5c9b
    override val outline = Color.hex(0xed5c9b)

    // https://colorpicker.me/#3c3c3c
    override val text = Color.hex(0x3c3c3c)

    // https://colorpicker.me/#565656
    override val secondaryText = Color.hex(0x565656)

    // https://colorpicker.me/#70adc7
    override val secondaryBlue = Color.hex(0x70adc7)

    // https://colorpicker.me/#ff2020
    override val red = Color.hex(0xff2020)

    // https://colorpicker.me/#ff8f1f
    override val orange = Color.hex(0xff8f1f)

    // https://colorpicker.me/#38ff6e
    override val green = Color.hex(0x38ff6e)

    // https://colorpicker.me/#4dd3ff
    override val blue = Color.hex(0x4dd3ff)
}