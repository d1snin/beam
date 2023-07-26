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

package dev.d1s.beam.ui

import dev.d1s.beam.ui.client.buildBeamClient
import dev.d1s.beam.ui.component.*
import dev.d1s.beam.ui.theme.DefaultThemeHolder
import dev.d1s.beam.ui.theme.ThemeHolder
import dev.d1s.exkt.kvision.component.Component
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

object Qualifier {

    val HeadingComponent = named("heading-component")
    val IconComponent = named("icon-component")
    val DaemonStatusComponent = named("daemon-status-component")
    val SpaceInfoComponent = named("space-info-component")
}

fun setupModule() {
    startKoin {
        modules(mainModule)
    }
}

private val mainModule = module {
    beamClient()
    themeHolder()
    components()
}

private fun Module.beamClient() {
    single {
        buildBeamClient()
    }
}

private fun Module.themeHolder() {
    singleOf<ThemeHolder>(::DefaultThemeHolder)
}

private fun Module.components() {
    singleOf<Component.Root>(::RootComponent)

    singleOf<Component<Unit>>(::HeadingComponent) {
        qualifier = Qualifier.HeadingComponent
    }

    singleOf<Component<Unit>>(::IconComponent) {
        qualifier = Qualifier.IconComponent
    }

    singleOf<Component<Unit>>(::SpaceInfoComponent) {
        qualifier = Qualifier.SpaceInfoComponent
    }

    singleOf<Component<Unit>>(::DaemonStatusComponent) {
        qualifier = Qualifier.DaemonStatusComponent
    }
}