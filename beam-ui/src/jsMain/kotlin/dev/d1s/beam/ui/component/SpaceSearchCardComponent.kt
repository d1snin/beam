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

import dev.d1s.beam.ui.util.Size
import dev.d1s.exkt.kvision.component.Component
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import io.kvision.utils.px
import kotlinx.atomicfu.atomic
import org.koin.core.component.KoinComponent

class SpaceSearchCardComponent : Component<SpaceSearchCardComponent.Config>(::Config), KoinComponent {

    private val contents by lazy {
        getKoin().getAll<SpaceSearchCardContent>()
    }

    private val content: SpaceSearchCardContent
        get() {
            val mode = config.mode.value

            return contents.find {
                it.mode == mode
            } ?: error("$mode is unsupported")
        }

    override fun SimplePanel.render() {
        div(className = "container d-flex justify-content-center") {
            maxWidth = Size.Lg.px

            card(className = "d-flex flex-column justify-content-center w-100 p-5") {
                image()
                text()
            }
        }
    }

    private fun SimplePanel.image() {
        with(content) {
            image()
        }
    }

    private fun SimplePanel.text() {
        with(content) {
            text()
        }
    }

    enum class Mode {

        NOT_FOUND, EMPTY_SPACE
    }

    class Config {

        val mode = atomic(Mode.NOT_FOUND)
    }
}