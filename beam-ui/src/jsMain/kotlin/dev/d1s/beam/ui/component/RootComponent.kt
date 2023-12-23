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

import dev.d1s.beam.commons.spaceBackground
import dev.d1s.beam.commons.spaceBackgroundFixed
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.theme.currentTheme
import dev.d1s.beam.ui.theme.setBackground
import dev.d1s.beam.ui.theme.setTextColor
import dev.d1s.beam.ui.util.currentSpace
import dev.d1s.beam.ui.util.currentTranslationObservable
import dev.d1s.exkt.kvision.bootstrap.dFlex
import dev.d1s.exkt.kvision.bootstrap.flexColumn
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import dev.d1s.exkt.kvision.component.render
import io.kvision.core.*
import io.kvision.panel.SimplePanel
import io.kvision.state.bind
import io.kvision.utils.vh
import kotlinx.browser.document
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.w3c.dom.css.CSSStyleDeclaration

class RootComponent : Component.Root(), KoinComponent {

    private val headingComponent by inject<Component<Unit>>(Qualifier.HeadingComponent)

    private val spaceContentComponent by inject<Component<Unit>>(Qualifier.SpaceContentComponent)

    private val footerComponent by inject<Component<Unit>>(Qualifier.FooterComponent)

    override fun SimplePanel.render(): Effect {
        bind(currentTranslationObservable) {
            renderRoot()
        }

        return Effect.Success
    }

    private fun SimplePanel.renderRoot() {
        sizing()
        display()
        font()
        linkStyle()
        background()
        components()
    }

    private fun SimplePanel.sizing() {
        minHeight = 100.vh

        bodyStyle {
            minHeight = "100vh"
            height = "100vh"
        }
    }

    private fun SimplePanel.display() {
        dFlex()
        flexColumn()
    }

    private fun SimplePanel.font() {
        setTextColor()
    }

    private fun SimplePanel.linkStyle() {
        fun setStyle(pClass: PClass? = null, block: Style.() -> Unit) {
            style("a", pClass) {
                block()
            }
        }

        setStyle {
            color = currentTheme.secondaryBlue
        }

        setStyle(PClass.LINK) {
            textDecoration = TextDecoration(TextDecorationLine.NONE)
        }

        setStyle(PClass.HOVER) {
            textDecoration = TextDecoration(TextDecorationLine.UNDERLINE)
        }
    }

    private fun background() {
        bodyStyle {
            setBackground()

            currentSpace?.let { space ->
                val backgroundUrl = space.metadata.spaceBackground

                backgroundUrl?.let {
                    backgroundImage(url = it, fixed = space.metadata.spaceBackgroundFixed)
                }
            }
        }
    }

    private fun CSSStyleDeclaration.backgroundImage(url: String, fixed: Boolean) {
        backgroundImage = "url($url)"
        backgroundSize = "cover"
        backgroundRepeat = "no-repeat"

        if (fixed) {
            backgroundPosition = "center"
            backgroundAttachment = "fixed"
        }
    }

    private fun SimplePanel.components() {
        render(headingComponent)
        render(spaceContentComponent)
        render(footerComponent)
    }

    private fun bodyStyle(block: CSSStyleDeclaration.() -> Unit) {
        requireNotNull(document.body).style.apply(block)
    }
}