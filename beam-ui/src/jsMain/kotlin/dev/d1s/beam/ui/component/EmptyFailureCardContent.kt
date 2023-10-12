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
import dev.d1s.beam.ui.util.currentTranslation
import dev.d1s.beam.ui.util.failureCardEmptySpaceIconAlt
import dev.d1s.beam.ui.util.failureCardEmptySpaceMessage
import io.kvision.html.image
import io.kvision.panel.SimplePanel
import io.kvision.utils.perc
import org.koin.core.component.KoinComponent

class EmptyFailureCardContent : FailureCardContent(), KoinComponent {

    override val mode = FailureCardComponent.Mode.EMPTY_SPACE

    override fun SimplePanel.image() {
        image(
            currentTheme.emptySpaceIcon,
            alt = currentTranslation.failureCardEmptySpaceIconAlt,
            className = "mb-4 mb-lg-5 align-self-center"
        ) {
            width = 35.perc
        }
    }

    override fun SimplePanel.text() {
        failureCardText(currentTranslation.failureCardEmptySpaceMessage)
    }
}