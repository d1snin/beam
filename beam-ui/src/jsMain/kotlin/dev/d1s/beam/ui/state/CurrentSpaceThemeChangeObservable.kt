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

package dev.d1s.beam.ui.state

import dev.d1s.beam.commons.SpaceThemeDefinition
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.theme.ThemeHolder
import dev.d1s.beam.ui.theme.currentTheme
import io.kvision.state.ObservableValue
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext

class CurrentSpaceThemeChangeObservable : Observable<SpaceThemeDefinition>, KoinComponent {

    override val state = ObservableValue(SpaceThemeDefinition.Fallback)

    private val themeHolder by inject<ThemeHolder>()

    private val currentSpaceChangeObservable by inject<Observable<CurrentSpaceChange>>(Qualifier.CurrentSpaceChangeObservable)

    override fun monitor() = launchMonitor(loop = false) {
        currentSpaceChangeObservable.state.subscribe { _ ->
            themeHolder.actualizeTheme()

            val currentTheme = themeHolder.current

            state.setState(currentTheme.definition)
        }
    }
}

private val currentSpaceThemeChangeObservable by lazy {
    GlobalContext.get().get<Observable<SpaceThemeDefinition>>(Qualifier.CurrentSpaceThemeChangeObservable)
}

fun bindToCurrentTheme(block: (SpaceThemeDefinition) -> Unit) {
    currentSpaceThemeChangeObservable.state.subscribe { definition ->
        block(definition)
    }
}