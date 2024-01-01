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

import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.contententity.renderStyledText
import dev.d1s.beam.ui.theme.setSecondaryText
import dev.d1s.beam.ui.util.currentSpace
import dev.d1s.beam.ui.util.currentTranslation
import dev.d1s.beam.ui.util.defaultRemark
import dev.d1s.beam.ui.util.renderFluidContainer
import dev.d1s.exkt.kvision.bootstrap.*
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import dev.d1s.exkt.kvision.component.render
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import io.kvision.utils.rem
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FooterComponent : Component<Unit>(), KoinComponent {

    private val languageSwitcherComponent by inject<Component<Unit>>(Qualifier.LanguageSwitcherComponent)

    override fun SimplePanel.render(): Effect {
        renderFluidContainer {
            pt5()
            pb2()
            mtAuto()
            dFlex()
            justifyContentBetween()
            alignItemsCenter()

            renderRemark()
            renderLanguageSwitcherComponent()
        }

        return Effect.Success
    }

    private fun SimplePanel.renderRemark() {
        div {
            fontSize = 0.85.rem
            setSecondaryText()

            val remark = currentSpace?.view?.remark ?: currentTranslation.defaultRemark
            renderStyledText(remark)
        }
    }

    private fun SimplePanel.renderLanguageSwitcherComponent() {
        render(languageSwitcherComponent)
    }
}