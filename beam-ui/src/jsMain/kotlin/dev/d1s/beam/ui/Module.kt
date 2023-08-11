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

import dev.d1s.beam.commons.Blocks
import dev.d1s.beam.ui.client.DaemonConnector
import dev.d1s.beam.ui.client.DaemonStatusWithPing
import dev.d1s.beam.ui.client.DefaultDaemonConnector
import dev.d1s.beam.ui.client.buildBeamClient
import dev.d1s.beam.ui.component.*
import dev.d1s.beam.ui.contententity.ContentEntityRenderer
import dev.d1s.beam.ui.contententity.TextContentEntityRenderer
import dev.d1s.beam.ui.contententity.VoidContentEntityRenderer
import dev.d1s.beam.ui.state.*
import dev.d1s.beam.ui.theme.DefaultThemeHolder
import dev.d1s.beam.ui.theme.ThemeHolder
import dev.d1s.exkt.kvision.component.Component
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

object Qualifier {

    val DaemonStatusObservable = named("daemon-status-observable")
    val DaemonStatusWithPingObservable = named("daemon-status-with-ping-observable")
    val CurrentSpaceChangeObservable = named("current-space-change-observable")
    val CurrentSpaceContentChangeObservable = named("current-space-content-change-observable")

    val HeadingComponent = named("heading-component")
    val IconComponent = named("icon-component")
    val SpaceInfoComponent = named("space-info-component")
    val DaemonStatusComponent = named("daemon-status-component")
    val SpaceContentComponent = named("space-content-component")
    val BlockContainerComponent = named("block-container-component")
    val BlockComponent = named("block-component")
    val DisconnectedDaemonStatusBlankslateComponent = named("disconnected-daemon-status-blankslate-component")
    val SpaceSearchCardComponent = named("space-search-card-component")

    val VoidContentEntityRenderer = named("void-content-entity-renderer")
    val TextContentEntityRenderer = named("text-content-entity-renderer")

    val NormalSpaceSearchCardContent = named("normal-space-search-card-content")
    val NotFoundSpaceSearchCardContent = named("not-found-space-search-card-content")
    val EmptySpaceSearchCardContent = named("empty-space-search-card-content")
}

fun setupModule() {
    startKoin {
        modules(mainModule)
    }
}

private val mainModule = module {
    beamClient()
    daemonConnector()
    themeHolder()
    observables()
    components()
    contentEntityRenderers()
    spaceSearchCardContents()
}

private fun Module.beamClient() {
    single {
        buildBeamClient()
    }
}

private fun Module.daemonConnector() {
    singleOf<DaemonConnector>(::DefaultDaemonConnector)
}

private fun Module.themeHolder() {
    singleOf<ThemeHolder>(::DefaultThemeHolder)
}

private fun Module.observables() {
    singleOf<ObservableLauncher>(::DefaultObservableLauncher)

    singleOf<Observable<DaemonStatusWithPing?>>(::DaemonStatusObservable) {
        qualifier = Qualifier.DaemonStatusObservable
    }

    singleOf<Observable<DaemonStatusWithPing?>>(::DaemonStatusWithPingObservable) {
        qualifier = Qualifier.DaemonStatusWithPingObservable
    }

    singleOf<Observable<CurrentSpaceChange>>(::CurrentSpaceChangeObservable) {
        qualifier = Qualifier.CurrentSpaceChangeObservable
    }

    singleOf<Observable<Blocks?>>(::CurrentSpaceContentChangeObservable) {
        qualifier = Qualifier.CurrentSpaceContentChangeObservable
    }
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

    singleOf<Component<Unit>>(::SpaceContentComponent) {
        qualifier = Qualifier.SpaceContentComponent
    }

    singleOf<Component<Unit>>(::BlockContainerComponent) {
        qualifier = Qualifier.BlockContainerComponent
    }

    factory<Component<BlockComponent.Config>>(qualifier = Qualifier.BlockComponent) {
        BlockComponent()
    }

    singleOf<Component<Unit>>(::DisconnectedDaemonStatusBlankslateComponent) {
        qualifier = Qualifier.DisconnectedDaemonStatusBlankslateComponent
    }

    singleOf<Component<SpaceSearchCardComponent.Config>>(::SpaceSearchCardComponent) {
        qualifier = Qualifier.SpaceSearchCardComponent
    }
}

private fun Module.contentEntityRenderers() {
    singleOf<ContentEntityRenderer>(::VoidContentEntityRenderer) {
        qualifier = Qualifier.VoidContentEntityRenderer
    }

    singleOf<ContentEntityRenderer>(::TextContentEntityRenderer) {
        qualifier = Qualifier.TextContentEntityRenderer
    }
}

private fun Module.spaceSearchCardContents() {
    singleOf<SpaceSearchCardContent>(::NormalSpaceSearchCardContent) {
        qualifier = Qualifier.NormalSpaceSearchCardContent
    }

    singleOf<SpaceSearchCardContent>(::NotFoundSpaceSearchCardContent) {
        qualifier = Qualifier.NotFoundSpaceSearchCardContent
    }

    singleOf<SpaceSearchCardContent>(::EmptySpaceSearchCardContent) {
        qualifier = Qualifier.EmptySpaceSearchCardContent
    }
}