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

import dev.d1s.beam.commons.Space
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.resource.ResourceLocation
import dev.d1s.beam.ui.util.*
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import dev.d1s.exkt.kvision.component.render
import io.kvision.html.image
import io.kvision.panel.SimplePanel
import io.kvision.utils.px
import kotlinx.atomicfu.atomic
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class SpaceCardComponent : Component<SpaceCardComponent.Config>(::Config), KoinComponent {

    private val space
        get() = config.space.value ?: currentSpace

    override fun SimplePanel.render(): Effect {
        val descriptiveCardComponent =
            get<Component<DescriptiveCardComponent.Config>>(Qualifier.DescriptiveCardComponent)

        render(descriptiveCardComponent) {
            setTitle()
            setDescription()
            setUrl()
            setBare()
            setCardPadding()
            setEnableHeading()
            setCardFullWidth()
            setIcon()
            setImage()
        }

        return Effect.Success
    }

    private fun DescriptiveCardComponent.Config.setTitle() {
        val spaceTitle = space?.view?.title ?: currentTranslation.defaultTitle
        title.value = spaceTitle
    }

    private fun DescriptiveCardComponent.Config.setDescription() {
        space?.view?.description?.let {
            description.value = it
        }
    }

    private fun DescriptiveCardComponent.Config.setUrl() {
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

    private fun DescriptiveCardComponent.Config.setCardFullWidth() {
        config.cardFullWidth.value?.let {
            cardFullWidth.value = it
        }
    }

    private fun DescriptiveCardComponent.Config.setIcon() {
        icon.value = "bi bi-box-arrow-up-right"
    }

    private fun DescriptiveCardComponent.Config.setImage() {
        image {
            image(space?.view?.icon ?: ResourceLocation.ICON, alt = currentTranslation.iconAlt) {
                width = config.iconWidth.value
            }
        }
    }

    // Господи. Мне страшно. Я боюсь за все.
    // За себя. За нее. За наше будущее.
    // Что с нами будет?

    class Config {

        val space = atomic<Space?>(null)

        val bare = atomic<Boolean?>(null)

        val cardPaddingLevel = atomic<Int?>(null)
        val cardStartPaddingLevel = atomic<Int?>(null)

        val iconWidth = atomic(30.px)

        val enableHeading = atomic<Boolean?>(null)

        val cardFullWidth = atomic<Boolean?>(null)
    }
}