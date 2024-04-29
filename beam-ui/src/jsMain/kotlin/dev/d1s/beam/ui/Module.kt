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

package dev.d1s.beam.ui

import dev.d1s.beam.commons.BlockSize
import dev.d1s.beam.ui.client.DaemonConnector
import dev.d1s.beam.ui.client.DaemonStatusWithPing
import dev.d1s.beam.ui.client.DefaultDaemonConnector
import dev.d1s.beam.ui.client.buildBeamClient
import dev.d1s.beam.ui.component.*
import dev.d1s.beam.ui.contententity.*
import dev.d1s.beam.ui.state.*
import dev.d1s.beam.ui.theme.CurrentTheme
import dev.d1s.beam.ui.theme.DefaultCurrentTheme
import dev.d1s.beam.ui.util.buildSpaceUrl
import dev.d1s.exkt.kvision.component.Component
import io.ktor.client.*
import io.ktor.client.plugins.*
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
    val DescriptiveCardComponent = named("descriptive-card-component")
    val SpaceCardComponent = named("space-card-component")
    val SpaceListingComponent = named("space-listing-component")
    val ExploreOffCanvasComponent = named("explore-dropdown-component")
    val SpaceContentComponent = named("space-content-component")
    val BlockContainerComponent = named("block-container-component")
    val BlockComponent = named("block-component")
    val FailureCardComponent = named("failure-card-component")
    val FooterComponent = named("footer-component")
    val LanguageSwitcherComponent = named("language-switcher-component")

    val VoidContentEntityRenderer = named("void-content-entity-renderer")
    val TextContentEntityRenderer = named("text-content-entity-renderer")
    val ButtonLinkContentEntityRenderer = named("button-link-content-entity-renderer")
    val AlertContentEntityRenderer = named("alert-content-entity-renderer")
    val SpaceContentEntityRenderer = named("space-content-entity-renderer")
    val ImageContentEntityRenderer = named("image-content-entity-renderer")
    val EmbedContentEntityRenderer = named("embed-content-entity-renderer")
    val FileContentEntityRenderer = named("file-content-entity-renderer")

    val NotFoundFailureCardContent = named("not-found-failure-card-content")
    val EmptyFailureCardContent = named("empty-failure-card-content")
    val LostConnectionFailureCardContent = named("lost-connection-failure-card-content")
}

fun setupModule() {
    startKoin {
        modules(mainModule)
    }
}

private val mainModule = module {
    beamClient()
    httpClient()
    daemonConnector()
    currentTheme()
    observables()
    components()
    contentEntityRenderers()
    styledTextRenderer()
    failureCardContents()
}

private fun Module.beamClient() {
    single {
        buildBeamClient()
    }
}

private fun Module.httpClient() {
    single {
        HttpClient {
            defaultRequest {
                url(buildSpaceUrl())
            }
        }
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

    singleOf<Observable<SpaceContentChange?>>(::CurrentSpaceContentChangeObservable) {
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

    factoryOf<Component<DescriptiveCardComponent.Config>>(::DescriptiveCardComponent) {
        qualifier = Qualifier.DescriptiveCardComponent
    }

    factoryOf<Component<SpaceListingComponent.Config>>(::SpaceListingComponent) {
        qualifier = Qualifier.SpaceListingComponent
    }

    singleOf<Component<Unit>>(::ExploreOffCanvasComponent) {
        qualifier = Qualifier.ExploreOffCanvasComponent
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

    singleOf<Component<FailureCardComponent.Config>>(::FailureCardComponent) {
        qualifier = Qualifier.FailureCardComponent
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

    singleOf<ContentEntityRenderer>(::AlertContentEntityRenderer) {
        qualifier = Qualifier.AlertContentEntityRenderer
    }

    singleOf<ContentEntityRenderer>(::SpaceContentEntityRenderer) {
        qualifier = Qualifier.SpaceContentEntityRenderer
    }

    singleOf<ContentEntityRenderer>(::ImageContentEntityRenderer) {
        qualifier = Qualifier.ImageContentEntityRenderer
    }

    singleOf<ContentEntityRenderer>(::EmbedContentEntityRenderer) {
        qualifier = Qualifier.EmbedContentEntityRenderer
    }

    singleOf<ContentEntityRenderer>(::FileContentEntityRenderer) {
        qualifier = Qualifier.FileContentEntityRenderer
    }
}

private fun Module.styledTextRenderer() {
    singleOf<StyledTextRenderer>(::DefaultStyledTextRenderer)
}

private fun Module.failureCardContents() {
    singleOf<FailureCardContent>(::NotFoundFailureCardContent) {
        qualifier = Qualifier.NotFoundFailureCardContent
    }

    singleOf<FailureCardContent>(::EmptyFailureCardContent) {
        qualifier = Qualifier.EmptyFailureCardContent
    }

    singleOf<FailureCardContent>(::LostConnectionFailureCardContent) {
        qualifier = Qualifier.LostConnectionFailureCardContent
    }
}