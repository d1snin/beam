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

package dev.d1s.beam.ui.component

import dev.d1s.beam.client.BeamClient
import dev.d1s.beam.commons.Space
import dev.d1s.beam.commons.SpaceIdentifier
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.resource.ResourceLocation
import dev.d1s.beam.ui.util.*
import dev.d1s.exkt.kvision.bootstrap.w100
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import dev.d1s.exkt.kvision.component.render
import io.ktor.util.collections.*
import io.kvision.html.div
import io.kvision.html.image
import io.kvision.panel.SimplePanel
import io.kvision.utils.px
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

private val spaceCache = ConcurrentMap<SpaceIdentifier, Space?>()

class SpaceCardComponent : Component<SpaceCardComponent.Config>(::Config), KoinComponent {

    private val client by inject<BeamClient>()

    private val fetchingScope = CoroutineScope(Dispatchers.Main)

    override fun SimplePanel.render(): Effect {
        val descriptiveCardComponent =
            get<Component<DescriptiveCardComponent.Config>>(Qualifier.DescriptiveCardComponent)

        asyncDiv {
            val space = getSpace()

            render(descriptiveCardComponent) {
                setTitle(space)
                setDescription(space)
                setUrl(space)
                setBare()
                setCardPadding()
                setEnableHeading()
                setSize()
                setIcon()
                setImage(space)
            }
        }

        return Effect.Success
    }

    private fun SimplePanel.asyncDiv(block: suspend SimplePanel.() -> Unit) {
        div {
            w100()

            fetchingScope.launch {
                block()
            }
        }
    }

    private suspend fun getSpace() =
        config.spaceIdentifier.value?.let {
            spaceCache.getOrPut(it) {
                client.getSpace(it, currentLanguageCode).getOrNull()
            }
        } ?: currentSpace

    private fun DescriptiveCardComponent.Config.setTitle(space: Space?) {
        val spaceTitle = space?.view?.title ?: currentTranslation.defaultTitle
        title.value = spaceTitle
    }

    private fun DescriptiveCardComponent.Config.setDescription(space: Space?) {
        space?.view?.description?.let {
            description.value = it
        }
    }

    private fun DescriptiveCardComponent.Config.setUrl(space: Space?) {
        val spaceUrl = space?.let { buildSpaceUrl(it.slug) } ?: currentSpaceUrl
        url.value = spaceUrl
    }

    private fun DescriptiveCardComponent.Config.setBare() {
        config.bare.value?.let {
            bare.value = it
        }
    }

    private fun DescriptiveCardComponent.Config.setCardPadding() {
        config.cardPaddingLevel.value?.let {
            cardPaddingLevel.value = it
        }

        config.cardStartPaddingLevel.value?.let {
            cardStartPaddingLevel.value = it
        }
    }

    private fun DescriptiveCardComponent.Config.setEnableHeading() {
        config.enableHeading.value?.let {
            enableHeading.value = it
        }
    }

    private fun DescriptiveCardComponent.Config.setSize() {
        config.fullWidth.value?.let {
            fullWidth.value = it
        }

        config.fullHeight.value?.let {
            fullHeight.value = it
        }
    }

    private fun DescriptiveCardComponent.Config.setIcon() {
        icon.value = Icons.BOX_ARROW_UP_RIGHT
    }

    private fun DescriptiveCardComponent.Config.setImage(space: Space?) {
        image {
            image(space?.view?.icon ?: ResourceLocation.ICON, alt = currentTranslation.iconAlt) {
                width = config.iconWidth.value
            }
        }
    }

    class Config {

        val spaceIdentifier = atomic<String?>(null)

        val bare = atomic<Boolean?>(null)

        val cardPaddingLevel = atomic<Int?>(null)
        val cardStartPaddingLevel = atomic<Int?>(null)

        val iconWidth = atomic(30.px)

        val enableHeading = atomic<Boolean?>(null)

        val fullWidth = atomic<Boolean?>(null)
        val fullHeight = atomic<Boolean?>(null)
    }
}