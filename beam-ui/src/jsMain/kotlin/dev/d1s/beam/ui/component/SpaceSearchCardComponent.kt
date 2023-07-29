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

import dev.d1s.beam.client.PublicBeamClient
import dev.d1s.beam.commons.Role
import dev.d1s.beam.commons.Space
import dev.d1s.beam.ui.theme.currentTheme
import dev.d1s.beam.ui.theme.setBackground
import dev.d1s.beam.ui.theme.setTextColor
import dev.d1s.beam.ui.util.*
import dev.d1s.exkt.kvision.component.Component
import io.kvision.core.onClick
import io.kvision.core.onEvent
import io.kvision.html.*
import io.kvision.panel.SimplePanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.utils.event
import io.kvision.utils.px
import kotlinx.atomicfu.atomic
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SpaceSearchCardComponent : Component<SpaceSearchCardComponent.Config>(::Config), KoinComponent {

    private val client by inject<PublicBeamClient>()

    private val renderingScope = CoroutineScope(Dispatchers.Main)

    private val invalidInput = ObservableValue(false)

    private val contents by lazy {
        getKoin().getAll<SpaceSearchCardContent>()
    }

    private val content by lazy {
        val mode = config.mode.value

        contents.find {
            it.mode == mode
        } ?: error("$mode is unsupported")
    }

    override fun SimplePanel.render() {
        div(className = "container d-flex justify-content-center") {
            maxWidth = Breakpoint.MD.px

            card(className = "d-flex flex-column justify-content-center w-100 p-5") {
                image()
                text()
                searchBar()
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

        p {
            span(Texts.Body.SpaceSearchCard.SEARCH_HINT)
            latestSpacesHint()
            rootHint()
        }
    }

    private fun SimplePanel.latestSpacesHint() {
        initiallyInvisibleSecondaryText {
            val spaces = client.getSpaces(limit = LATEST_SPACES_LIMIT, offset = 0).getOrElse {
                return@initiallyInvisibleSecondaryText
            }

            val latestSpaces = spaces.elements.filter { space ->
                space.role != Role.ROOT
            }

            renderLatestSpaceLinks(latestSpaces)

            visible = true
        }
    }

    private fun SimplePanel.renderLatestSpaceLinks(latestSpaces: List<Space>) {
        if (latestSpaces.isNotEmpty() && currentSpace !in latestSpaces) {
            span(" ")
            span(Texts.Body.SpaceSearchCard.LATEST_SPACES_HINT)

            latestSpaces.forEach { space ->
                val slug = space.slug
                val url = buildSpaceUrl(slug)

                link(slug, url)

                if (space != latestSpaces.last()) {
                    span(", ")
                } else {
                    span(".")
                }
            }
        }
    }

    private fun SimplePanel.rootHint() {
        initiallyInvisibleSecondaryText {
            if (!isRootPath()) {
                span(Texts.Body.SpaceSearchCard.ROOT_HINT)

                val rootUrl = buildSpaceUrl()
                link("root", rootUrl)
                span(".")

                visible = true
            }
        }
    }

    private fun SimplePanel.initiallyInvisibleSecondaryText(block: suspend SimplePanel.() -> Unit) {
        secondaryText {
            visible = false

            renderingScope.launch {
                block()
            }
        }
    }

    private fun SimplePanel.secondaryText(block: SimplePanel.() -> Unit) {
        div {
            color = currentTheme.secondaryText
            block()
        }
    }

    private fun SimplePanel.searchBar() {
        div(className = "d-flex w-100") {
            input()
            goButton()
        }
    }

    private fun SimplePanel.input() {
        div(className = "form-floating w-75 me-2 me-md-3") {
            inputField()
            inputLabel()
        }
    }

    private fun SimplePanel.inputField() {
        input(InputType.TEXT, className = "form-control").bind(invalidInput) { invalid ->
            if (invalid) {
                addCssClass("is-invalid")
            }

            setTextColor()
            setBackground()

            id = INPUT_TEXT_ID
            required = true
            placeholder = Texts.Body.SpaceSearchCard.PLACEHOLDER
            setAttribute("aria-label", Texts.Body.SpaceSearchCard.PLACEHOLDER)

            handleEnterKeyEvent()
        }
    }

    private fun SimplePanel.handleEnterKeyEvent() {
        onEvent {
            event("keyup") { event ->
                event.preventDefault()

                if (event.asDynamic().keyCode == ENTER_KEY_CODE) {
                    redirectUserOrMarkInvalid()
                }
            }
        }
    }

    private fun SimplePanel.inputLabel() {
        label(forId = INPUT_TEXT_ID) {
            setStyle("--bs-body-bg", currentTheme.background.asString())

            span {
                color = currentTheme.text
                +Texts.Body.SpaceSearchCard.PLACEHOLDER
            }
        }
    }

    private fun SimplePanel.goButton() {
        input(InputType.BUTTON, "btn ${currentTheme.buttonClass} w-25") {
            value = Texts.Body.SpaceSearchCard.GO_BUTTON_VALUE

            onClick {
                redirectUserOrMarkInvalid()
            }
        }
    }

    private fun redirectUserOrMarkInvalid() {
        val spaceIdentifier = document.getElementById(INPUT_TEXT_ID)?.asDynamic()?.value as? String ?: ""

        if (spaceIdentifier.isBlank()) {
            invalidInput.setState(true)
        } else {
            window.location.href = buildSpaceUrl(spaceIdentifier)
        }
    }

    enum class Mode {

        NORMAL, NOT_FOUND, EMPTY_SPACE
    }

    class Config {

        val mode = atomic(Mode.NORMAL)
    }

    private companion object {

        private const val LATEST_SPACES_LIMIT = 5

        private const val INPUT_TEXT_ID = "spaceSearchInput"

        private const val ENTER_KEY_CODE = 13
    }
}