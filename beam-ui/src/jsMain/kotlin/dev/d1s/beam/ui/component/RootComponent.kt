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
import dev.d1s.beam.ui.state.CurrentSpaceChange
import dev.d1s.beam.ui.state.Observable
import dev.d1s.beam.ui.theme.setBackground
import dev.d1s.beam.ui.theme.setTextColor
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.render
import io.kvision.panel.SimplePanel
import io.kvision.utils.vh
import kotlinx.browser.document
import kotlinx.browser.window
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.w3c.dom.css.CSSStyleDeclaration

class RootComponent : Component.Root(), KoinComponent {

    private val currentSpaceChangeObservable by inject<Observable<CurrentSpaceChange>>(Qualifier.CurrentSpaceChangeObservable)

    private val headingComponent by inject<Component<Unit>>(Qualifier.HeadingComponent)

    private val spaceContentComponent by inject<Component<Unit>>(Qualifier.SpaceContentComponent)

    private val footerComponent by inject<Component<Unit>>(Qualifier.FooterComponent)

    override fun SimplePanel.render() {
        title()
        sizing()
        display()
        font()
        background()
        components()
    }

    private fun title() {
        val document = window.top.document

        currentSpaceChangeObservable.state.subscribe {
            val space = it.space

            document.title = space?.view?.title ?: "Beam Space not available"
        }
    }

    private fun SimplePanel.sizing() {
        minHeight = 100.vh

        bodyStyle {
            minHeight = "100vh"
            height = "100vh"
        }
    }

    private fun SimplePanel.display() {
        addCssClass("d-flex")
        addCssClass("flex-column")
    }

    private fun SimplePanel.font() {
        setTextColor()
    }

    private fun background() {
        bodyStyle {
            setBackground()
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