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

import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.util.Size
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import dev.d1s.exkt.kvision.component.render
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.utils.px
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FailureCardComponent : Component<FailureCardComponent.Config>(::Config), KoinComponent {

    private val spaceListingComponent by inject<Component<Unit>>(Qualifier.SpaceListingComponent)

    private val contents by lazy {
        getKoin().getAll<FailureCardContent>()
    }

    override fun SimplePanel.render(): Effect {
        div().bind(config.mode) { mode ->
            if (mode != Mode.NONE) {
                val content = getContent(mode)

                renderCardInContainer {
                    image(content)
                    text(content)

                    if (mode != Mode.LOST_CONNECTION) {
                        spaceListing()
                    }
                }
            }
        }

        return Effect.Success
    }

    private fun getContent(mode: Mode) =
        contents.find {
            it.mode == mode
        } ?: error("$mode is unsupported")

    private fun SimplePanel.renderCardInContainer(block: SimplePanel.() -> Unit) {
        div(className = "container d-flex justify-content-center") {
            maxWidth = Size.Lg.px

            renderCard(className = "d-flex flex-column justify-content-center w-100 p-4 pt-5") {
                block()
            }
        }
    }

    private fun SimplePanel.image(content: FailureCardContent) {
        with(content) {
            image()
        }
    }

    private fun SimplePanel.text(content: FailureCardContent) {
        with(content) {
            text()
        }
    }

    private fun SimplePanel.spaceListing() {
        render(spaceListingComponent)
    }

    enum class Mode {

        NONE, NOT_FOUND, EMPTY_SPACE, LOST_CONNECTION
    }

    class Config {

        val mode = ObservableValue(Mode.NONE)
    }
}