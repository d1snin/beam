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

package dev.d1s.beam.commons

public typealias SpaceThemeName = String
public typealias Color = Int

public sealed class SpaceThemeDefinition(public val name: SpaceThemeName) {

    public data object AccentDark : SpaceThemeDefinition("accent-dark")

    public data object GoldLight : SpaceThemeDefinition("gold-light")

    public data object GoldDark : SpaceThemeDefinition("gold-dark")

    public data object SkyLight : SpaceThemeDefinition("sky-light")

    public data object Barbie : SpaceThemeDefinition("barbie")

    public data object CatppuccinLatte : SpaceThemeDefinition("catppuccin-latte")

    public data object CatppuccinFrappe : SpaceThemeDefinition("catppuccin-frappe")

    public data object CatppuccinMacchiato : SpaceThemeDefinition("catppuccin-macchiato")

    public data object CatppuccinMocha : SpaceThemeDefinition("catppuccin-mocha")

    public companion object {

        public val Fallback: SpaceThemeDefinition = AccentDark

        public val definitions: List<SpaceThemeDefinition> =
            listOf(
                AccentDark,
                GoldLight,
                GoldDark,
                SkyLight,
                Barbie,
                CatppuccinLatte,
                CatppuccinFrappe,
                CatppuccinMacchiato,
                CatppuccinMocha
            )

        public fun byName(name: SpaceThemeName): SpaceThemeDefinition? =
            definitions.find {
                it.name == name
            }
    }
}