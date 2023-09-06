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

import dev.d1s.beam.commons.BlockSize
import dev.d1s.beam.commons.Blocks
import dev.d1s.beam.ui.client.DaemonConnector
import dev.d1s.beam.ui.client.DaemonStatusWithPing
import dev.d1s.beam.ui.client.DefaultDaemonConnector
import dev.d1s.beam.ui.client.buildBeamClient
import dev.d1s.beam.ui.component.*
import dev.d1s.beam.ui.contententity.*
import dev.d1s.beam.ui.state.*
import dev.d1s.beam.ui.theme.CurrentTheme
import dev.d1s.beam.ui.theme.DefaultCurrentTheme
import dev.d1s.exkt.kvision.component.Component
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

object Qualifier {

    val DaemonStatusWithPingObservable = named("daemon-status-with-ping-observable")
    val CurrentSpaceContentChangeObservable = named("current-space-content-change-observable")
    val MaxBlockSizeChangeObservable = named("max-block-size-change-observable")

    val HeadingComponent = named("heading-component")
    val SpaceCardComponent = named("space-card-component")
    val SpaceListingComponent = named("space-listing-component")
    val ExploreDropdownComponent = named("explore-dropdown-component")
    val DaemonStatusComponent = named("daemon-status-component")
    val SpaceContentComponent = named("space-content-component")
    val BlockContainerComponent = named("block-container-component")
    val BlockComponent = named("block-component")
    val SpaceFailureCardComponent = named("space-failure-card-component")
    val FooterComponent = named("footer-component")
    val LanguageSwitcherComponent = named("language-switcher-component")

    val VoidContentEntityRenderer = named("void-content-entity-renderer")
    val TextContentEntityRenderer = named("text-content-entity-renderer")
    val ButtonLinkContentEntityRenderer = named("button-link-content-entity-renderer")
    val SpaceContentEntityRenderer = named("space-content-entity-renderer")
    val ImageContentEntityRenderer = named("image-content-entity-renderer")

    val NotFoundSpaceFailureCardContent = named("not-found-space-failure-card-content")
    val EmptySpaceFailureCardContent = named("empty-space-failure-card-content")
}

fun setupModule() {
    startKoin {
        modules(mainModule)
    }
}

private val mainModule = module {
    beamClient()
    daemonConnector()
    currentTheme()
    observables()
    components()
    contentEntityRenderers()
    spaceFailureCardContents()
}

private fun Module.beamClient() {
    single {
        buildBeamClient()
    }
}

private fun Module.daemonConnector() {
    singleOf<DaemonConnector>(::DefaultDaemonConnector)
}

private fun Module.currentTheme() {
    singleOf<CurrentTheme>(::DefaultCurrentTheme)
}

private fun Module.observables() {
    singleOf<ObservableLauncher>(::DefaultObservableLauncher)

    singleOf<Observable<DaemonStatusWithPing?>>(::DaemonStatusWithPingObservable) {
        qualifier = Qualifier.DaemonStatusWithPingObservable
    }

    singleOf<Observable<Blocks?>>(::CurrentSpaceContentChangeObservable) {
        qualifier = Qualifier.CurrentSpaceContentChangeObservable
    }

    singleOf<Observable<BlockSize>>(::MaxBlockSizeChangeObservable) {
        qualifier = Qualifier.MaxBlockSizeChangeObservable
    }
}

private fun Module.components() {
    singleOf<Component.Root>(::RootComponent)

    singleOf<Component<Unit>>(::HeadingComponent) {
        qualifier = Qualifier.HeadingComponent
    }

    factoryOf<Component<SpaceCardComponent.Config>>(::SpaceCardComponent) {
        qualifier = Qualifier.SpaceCardComponent
    }

    singleOf<Component<Unit>>(::SpaceListingComponent) {
        qualifier = Qualifier.SpaceListingComponent
    }

    singleOf<Component<Unit>>(::ExploreDropdownComponent) {
        qualifier = Qualifier.ExploreDropdownComponent
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

    factoryOf<Component<BlockComponent.Config>>(::BlockComponent) {
        qualifier = Qualifier.BlockComponent
    }

    singleOf<Component<SpaceFailureCardComponent.Config>>(::SpaceFailureCardComponent) {
        qualifier = Qualifier.SpaceFailureCardComponent
    }

    singleOf<Component<Unit>>(::FooterComponent) {
        qualifier = Qualifier.FooterComponent
    }

    singleOf<Component<Unit>>(::LanguageSwitcherComponent) {
        qualifier = Qualifier.LanguageSwitcherComponent
    }
}

private fun Module.contentEntityRenderers() {
    singleOf<ContentEntityRenderer>(::VoidContentEntityRenderer) {
        qualifier = Qualifier.VoidContentEntityRenderer
    }

    singleOf<ContentEntityRenderer>(::TextContentEntityRenderer) {
        qualifier = Qualifier.TextContentEntityRenderer
    }

    singleOf<ContentEntityRenderer>(::ButtonLinkContentEntityRenderer) {
        qualifier = Qualifier.ButtonLinkContentEntityRenderer
    }

    singleOf<ContentEntityRenderer>(::SpaceContentEntityRenderer) {
        qualifier = Qualifier.SpaceContentEntityRenderer
    }

    singleOf<ContentEntityRenderer>(::ImageContentEntityRenderer) {
        qualifier = Qualifier.ImageContentEntityRenderer
    }
}

private fun Module.spaceFailureCardContents() {
    singleOf<SpaceFailureCardContent>(::NotFoundSpaceFailureCardContent) {
        qualifier = Qualifier.NotFoundSpaceFailureCardContent
    }

    singleOf<SpaceFailureCardContent>(::EmptySpaceFailureCardContent) {
        qualifier = Qualifier.EmptySpaceFailureCardContent
    }
}