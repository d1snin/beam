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

import dev.d1s.beam.commons.VERSION
import dev.d1s.beam.ui.theme.setSecondaryBlue
import dev.d1s.beam.ui.theme.setSecondaryText
import dev.d1s.beam.ui.util.*
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.Effect
import io.kvision.html.div
import io.kvision.html.link
import io.kvision.html.span
import io.kvision.panel.SimplePanel
import io.kvision.utils.rem
import org.koin.core.component.KoinComponent

class FooterComponent : Component<Unit>(), KoinComponent {

    override fun SimplePanel.render(): Effect {
        div(className = "container-fluid pt-5 pb-2 mt-auto d-flex flex-column align-items-start") {
            fontSize = 0.85.rem

            setSecondaryText()

            div {
                span(currentTranslation.footerMessageFirstPart)
                nbsp()
                span(currentTranslation.footerMessageSecondPart) {
                    setSecondaryBlue()
                }
                nbsp()
                span("v$VERSION")
            }

            div {
                link(currentTranslation.footerSourceCodeLinkMessage, currentTranslation.footerSourceCodeLinkUrl) {
                    setSecondaryText()
                }
            }
        }

        return Effect.Success
    }

    private fun SimplePanel.nbsp() =
        span {
            addAfterInsertHook {
                getElement()?.innerHTML = "&nbsp;"
            }
        }

    // Господи, ты просто пишешь и оставляешь меня без ответа на что-либо.
    // Могу ли я отвечать тебе тем же? Нет.
    // Я не знаю, что с тобой происходит. Я не хочу верить, что это то,
    // о чем ты мне говорила. Нет, нельзя верить в систематизацию проблемы.
    // Верь в обратное, но ты не можешь, я не понимаю и никогда не буду.
    // Я с тобой. Я понимаю, что нужно время. Я жду. Все будет хорошо.
}