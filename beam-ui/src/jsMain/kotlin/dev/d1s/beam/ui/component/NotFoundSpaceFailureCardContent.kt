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

import dev.d1s.beam.ui.state.bindToMaxBlockSize
import dev.d1s.beam.ui.theme.currentTheme
import dev.d1s.beam.ui.util.Texts
import io.kvision.html.image
import io.kvision.panel.SimplePanel
import org.koin.core.component.KoinComponent

class NotFoundSpaceFailureCardContent : SpaceFailureCardContent(), KoinComponent {

    override val mode = SpaceFailureCardComponent.Mode.NOT_FOUND

    override fun SimplePanel.image() {
        bindToMaxBlockSize {
            image(
                currentTheme.notFoundIcon,
                alt = Texts.Body.SpaceFailureCard.NotFoundMode.ICON_ALT,
                className = "w-100 mb-4 mb-lg-5"
            )
        }
    }

    override fun SimplePanel.text() {
        spaceFailureCardText(Texts.Body.SpaceFailureCard.NotFoundMode.TEXT)
    }
}