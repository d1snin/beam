/*
 * Copyright 2023-2024 Mikhail Titov
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
import dev.d1s.beam.ui.util.failureCardNotFoundIconAlt
import dev.d1s.beam.ui.util.failureCardNotFoundMessage
import dev.d1s.exkt.kvision.bootstrap.Breakpoint
import dev.d1s.exkt.kvision.bootstrap.mb4
import dev.d1s.exkt.kvision.bootstrap.mb5
import dev.d1s.exkt.kvision.bootstrap.w100
import io.kvision.html.image
import io.kvision.panel.SimplePanel
import org.koin.core.component.KoinComponent

class NotFoundFailureCardContent : FailureCardContent(), KoinComponent {

    override val mode = FailureCardComponent.Mode.NOT_FOUND

    override fun SimplePanel.image() {
        image(
            currentTheme.notFoundIcon,
            alt = currentTranslation.failureCardNotFoundIconAlt
        ) {
            w100()
            mb4()
            mb5(breakpoint = Breakpoint.LG)
        }
    }

    override fun SimplePanel.text() {
        failureCardText(currentTranslation.failureCardNotFoundMessage)
    }
}