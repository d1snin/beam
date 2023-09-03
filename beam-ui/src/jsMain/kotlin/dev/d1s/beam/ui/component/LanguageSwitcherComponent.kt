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
import dev.d1s.beam.commons.Translation
import dev.d1s.beam.commons.Translations
import dev.d1s.beam.ui.theme.currentTheme
import dev.d1s.beam.ui.theme.setOutline
import dev.d1s.beam.ui.theme.setOverlay
import dev.d1s.beam.ui.theme.setTextColor
import dev.d1s.beam.ui.util.*
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import io.kvision.core.onClick
import io.kvision.html.*
import io.kvision.panel.SimplePanel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LanguageSwitcherComponent : Component<Unit>(), KoinComponent {

    private val client by inject<PublicBeamClient>()

    private val renderingScope = CoroutineScope(Dispatchers.Default)

    override fun SimplePanel.render(): Effect {
        val (effectState, effect) = Effect.lazy()

        launchDiv {
            val translations = client.getTranslations(currentSpace?.id).getOrNull()
            if (translations == null || translations.size <= 1) {
                effectState.value = false
                return@launchDiv
            }

            renderDropup(translations)
        }

        return effect
    }

    private fun SimplePanel.launchDiv(block: suspend SimplePanel.() -> Unit) {
        div {
            renderingScope.launch {
                block()
            }
        }
    }

    private fun SimplePanel.renderDropup(translations: Translations) {
        div(className = "dropup") {
            renderButton()
            renderDropdownMenu(translations)
        }
    }

    private fun SimplePanel.renderButton() {
        button(
            text = "",
            style = currentTheme.buttonStyle,
            className = "btn-sm dropdown-toggle"
        ) {
            setButtonAttributes()

            iconWithMargin("bi bi-translate")
            +currentTranslation.footerLanguageSwitcherMessage
        }
    }

    private fun SimplePanel.renderDropdownMenu(translations: Translations) {
        ul(className = "dropdown-menu shadow") {
            setOutline()
            setOverlay()

            translations.filter { it != currentTranslation }.forEach { translation ->
                renderDropdownItem(translation)
            }
        }
    }

    private fun SimplePanel.setButtonAttributes() {
        setAttribute("data-bs-toggle", "dropdown")
        setAttribute("data-bs-offset", "0,10")
        setAttribute("aria-expanded", "false")
    }

    private fun SimplePanel.renderDropdownItem(translation: Translation) {
        renderDropdownItem {
            setStyle("--bs-dropdown-link-hover-bg", currentTheme.background.asString())
            setTextColor()

            setAttribute("type", "button")

            iconWithMargin("bi bi-globe-americas")
            +translation.languageName

            onClick {
                setCurrentTranslation(translation)
            }
        }
    }

    private fun SimplePanel.renderDropdownItem(block: SimplePanel. () -> Unit) {
        li {
            tag(TAG.BUTTON, className = "dropdown-item") {
                block()
            }
        }
    }
}