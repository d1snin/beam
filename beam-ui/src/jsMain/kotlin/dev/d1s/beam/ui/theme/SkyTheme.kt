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

object SkyTheme : AbstractTheme(SpaceThemeDefinition.Sky) {

    override val notFoundIcon = ResourceLocation.NOT_FOUND_DARK

    override val emptySpaceIcon = ResourceLocation.EMPTY_SPACE_DARK

    override val buttonStyle = ButtonStyle.OUTLINEDARK

    // https://colorpicker.me/#c8ebff
    override val background = Color.hex(0xc8ebff)

    // https://colorpicker.me/#e3f5ff
    override val overlay = Color.hex(0xe3f5ff)

    // https://colorpicker.me/#00a2ff
    override val outline = Color.hex(0x00a2ff)

    // https://colorpicker.me/#1b1b1b
    override val text = Color.hex(0x1b1b1b)

    // https://colorpicker.me/#3e3e3e
    override val secondaryText = Color.hex(0x3e3e3e)

    // https://colorpicker.me/#3989aa
    override val secondaryBlue = Color.hex(0x3989aa)

    // https://colorpicker.me/#ff2020
    override val red = Color.hex(0xff2020)

    // https://colorpicker.me/#ff8f1f
    override val orange = Color.hex(0xff8f1f)

    // https://colorpicker.me/#009c61
    override val green = Color.hex(0x009c61)

    // https://colorpicker.me/#feff9e
    override val yellow = Color.hex(0xfeff9e)

    // https://colorpicker.me/#009bce
    override val blue = Color.hex(0x009bce)
}