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

import dev.d1s.beam.commons.BlockSize
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.state.Observable
import dev.d1s.beam.ui.theme.currentTheme
import dev.d1s.beam.ui.theme.setOutline
import dev.d1s.beam.ui.theme.setOverlay
import dev.d1s.beam.ui.util.Size.sizeOf
import dev.d1s.beam.ui.util.currentTranslation
import dev.d1s.beam.ui.util.exploreDropdownCallout
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import dev.d1s.exkt.kvision.component.LazyEffect
import dev.d1s.exkt.kvision.component.render
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import io.kvision.state.bind
import io.kvision.utils.px
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ExploreDropdownComponent : Component<Unit>(), KoinComponent {

    private val maxBlockSizeChangeObservable by inject<Observable<BlockSize>>(Qualifier.MaxBlockSizeChangeObservable)

    private val spaceListingComponent by inject<Component<Unit>>(Qualifier.SpaceListingComponent)

    override fun SimplePanel.render(): Effect {
        div(className = "dropdown") {
            visible = false

            renderButton()
            renderMenu()
        }

        return Effect.Success
    }

    private fun SimplePanel.renderButton() {
        button(
            currentTranslation.exploreDropdownCallout,
            style = currentTheme.buttonStyle,
            className = "btn-sm dropdown-toggle"
        ) {
            setButtonAttributes()
        }
    }

    private fun SimplePanel.setButtonAttributes() {
        setAttribute("data-bs-toggle", "dropdown")
        setAttribute("data-bs-auto-close", "outside")
        setAttribute("data-bs-offset", "0,20")
        setAttribute("aria-expanded", "false")
    }

    private fun SimplePanel.renderMenu() {
        div(className = "dropdown-menu shadow p-3").bind(maxBlockSizeChangeObservable.state) { maxBlockSize ->
            setMenuWidth(maxBlockSize)
            setOutline()
            setOverlay()

            renderSpaceListingComponent(componentToBeVisible = this@renderMenu)
        }
    }

    private fun SimplePanel.setMenuWidth(maxBlockSize: BlockSize) {
        val maxBlockSizeValue = sizeOf(maxBlockSize)
        val minimizedMaxBlockSize = (maxBlockSizeValue * MENU_WIDTH_RATIO).toInt()

        width = minimizedMaxBlockSize.px
    }

    private fun SimplePanel.renderSpaceListingComponent(componentToBeVisible: SimplePanel) {
        val effect = render(spaceListingComponent)

        (effect as? LazyEffect)?.state?.subscribe { visible ->
            componentToBeVisible.visible = visible
        }
    }

    private companion object {

        private const val MENU_WIDTH_RATIO = 0.70
    }
}