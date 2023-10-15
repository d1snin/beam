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
import dev.d1s.beam.ui.util.currentSpace
import kotlinx.atomicfu.atomic
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext

interface CurrentTheme {

    val current: AbstractTheme
}

class DefaultCurrentTheme : CurrentTheme, KoinComponent {

    private val themes = listOf(
        AccentDarkTheme,
        GoldDarkTheme,
        SkyTheme,
        BarbieTheme,
        CatppuccinLatteTheme,
        CatppuccinFrappeTheme,
        CatppuccinMacchiatoTheme,
        CatppuccinMochaTheme
    )

    private val currentThemeDefinition = atomic(SpaceThemeDefinition.Fallback)

    init {
        currentSpace?.view?.theme?.let { themeName ->
            SpaceThemeDefinition.byName(themeName)?.let { definition ->
                currentThemeDefinition.value = definition
            }
        }
    }

    override val current: AbstractTheme
        get() = themes.find {
            it.definition == currentThemeDefinition.value
        } ?: error("No theme for definition $currentThemeDefinition")
}

val currentTheme: AbstractTheme
    get() = GlobalContext.get().get<CurrentTheme>().current