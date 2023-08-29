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

interface ThemeHolder {

    val current: AbstractTheme

    fun setCurrentTheme(theme: SpaceThemeDefinition)

    fun actualizeTheme()
}

class DefaultThemeHolder : ThemeHolder, KoinComponent {

    private val themes = listOf(
        AccentDarkTheme,
        BarbieTheme,
        CatppuccinMochaTheme
    )

    private val currentThemeDefinition = atomic(SpaceThemeDefinition.Fallback)

    override val current: AbstractTheme
        get() = themes.find {
            it.definition == currentThemeDefinition.value
        } ?: error("No theme for definition $currentThemeDefinition")

    override fun setCurrentTheme(theme: SpaceThemeDefinition) {
        this.currentThemeDefinition.value = theme
    }

    override fun actualizeTheme() {
        val themeName = currentSpace?.view?.theme

        val definition = themeName?.let {
            SpaceThemeDefinition.byName(themeName)
        } ?: SpaceThemeDefinition.Fallback

        setCurrentTheme(definition)
    }
}

val currentTheme: AbstractTheme
    get() = GlobalContext.get().get<ThemeHolder>().current