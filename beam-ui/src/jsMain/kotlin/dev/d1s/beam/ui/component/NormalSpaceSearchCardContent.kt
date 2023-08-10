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

import dev.d1s.beam.ui.theme.currentTheme
import dev.d1s.beam.ui.util.Size
import dev.d1s.beam.ui.util.Texts
import io.kvision.html.image
import io.kvision.panel.SimplePanel
import io.kvision.utils.perc
import io.kvision.utils.px
import io.kvision.utils.rem
import org.koin.core.component.KoinComponent

class NormalSpaceSearchCardContent : SpaceSearchCardContent, KoinComponent {

    override val mode = SpaceSearchCardComponent.Mode.NORMAL

    override fun SimplePanel.configureContainer() {
        maxWidth = Size.Md.px
        fontSize = 0.8.rem
    }

    override fun SimplePanel.image() {
        image(
            currentTheme.normalSpaceIcon,
            alt = Texts.Body.SpaceSearchCard.NormalMode.ICON_ALT,
            className = "mb-4 mb-lg-5 align-self-center"
        ) {
            width = 40.perc
        }
    }

    override fun SimplePanel.text() {
        spaceSearchCardText(Texts.Body.SpaceSearchCard.NormalMode.TEXT, fontSize = 1.1.rem)
    }
}