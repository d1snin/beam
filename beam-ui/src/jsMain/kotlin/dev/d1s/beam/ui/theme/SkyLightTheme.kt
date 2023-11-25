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
import dev.d1s.beam.ui.util.SpinnerStyle
import io.kvision.core.Color
import io.kvision.html.ButtonStyle

object SkyLightTheme : AbstractTheme(SpaceThemeDefinition.SkyLight) {

    override val notFoundIcon = ResourceLocation.NOT_FOUND_DARK

    override val emptySpaceIcon = ResourceLocation.EMPTY_SPACE_DARK

    override val lostConnectionIcon = ResourceLocation.LOST_CONNECTION_DARK

    override val buttonStyle = ButtonStyle.OUTLINEDARK

    override val spinnerStyle = SpinnerStyle.DARK

    // https://colorpicker.me/#fbfbfb
    override val background = Color.hex(0xfbfbfb)

    // https://colorpicker.me/#f4f4f4
    override val overlay = Color.hex(0xf4f4f4)

    // https://colorpicker.me/#00a4d9
    override val outline = Color.hex(0x00a4d9)

    // https://colorpicker.me/#d7ce4b
    override val text = Color.hex(0x181818)

    // https://colorpicker.me/#444444
    override val secondaryText = Color.hex(0x444444)

    // https://colorpicker.me/#3199c5
    override val secondaryBlue = Color.hex(0x3199c5)

    // https://colorpicker.me/#9f0000
    override val red = Color.hex(0x9f0000)

    // https://colorpicker.me/#ff7a00
    override val orange = Color.hex(0xff7a00)

    // https://colorpicker.me/#00ad2f
    override val green = Color.hex(0x00ad2f)

    // https://colorpicker.me/#feff9e
    override val yellow = Color.hex(0xfeff9e)

    // https://colorpicker.me/#00a4d9
    override val blue = Color.hex(0x00a4d9)
}