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
import io.kvision.core.Color

object AccentDarkTheme : AbstractTheme(SpaceThemeDefinition.AccentDark) {

    // https://colorpicker.me/#212121
    override val background = Color.hex(0x212121)

    // https://colorpicker.me/#232323
    override val overlay = Color.hex(0x232323)

    // https://colorpicker.me/#b4b4b4
    override val outline = Color.hex(0xb4b4b4)

    // https://colorpicker.me/#f0f0f0
    override val text = Color.hex(0xf0f0f0)

    // https://colorpicker.me/#89ffa9
    override val green = Color.hex(0x89ffa9)

    // https://colorpicker.me/#ff7878
    override val red = Color.hex(0xff7878)
}