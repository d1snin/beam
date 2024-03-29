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

package dev.d1s.beam.ui.util

import dev.d1s.beam.ui.theme.currentTheme
import dev.d1s.exkt.kvision.bootstrap.alignItemsCenter
import dev.d1s.exkt.kvision.bootstrap.dFlex
import dev.d1s.exkt.kvision.bootstrap.flexColumn
import dev.d1s.exkt.kvision.bootstrap.justifyContentCenter
import io.kvision.core.Display
import io.kvision.core.Widget
import io.kvision.html.div
import io.kvision.html.span
import io.kvision.panel.SimplePanel

private val processedResources = mutableSetOf<String>()

enum class SpinnerStyle(val code: String) {
    LIGHT("light"), DARK("dark")
}

fun SimplePanel.renderSpinnerOnLoading(resourceUrl: String, block: () -> Widget) {
    if (resourceUrl !in processedResources) {
        dFlex()
        flexColumn()
        justifyContentCenter()
        alignItemsCenter()

        div {
            dFlex()
            justifyContentCenter()

            val spinner = renderSpinner()
            val element = block()

            element.apply {
                addAfterInsertHook {
                    val stopSpinner: (event: dynamic) -> dynamic = {
                        spinner.display = Display.NONE
                        asDynamic()
                    }

                    getElement()?.apply {
                        onload = stopSpinner
                        onloadend = stopSpinner
                    }
                }
            }
        }

        processedResources += resourceUrl
    } else {
        block()
    }
}

fun SimplePanel.renderSpinner(): SimplePanel =
    div(className = "spinner-border my-3 text-${currentTheme.spinnerStyle.code}") {
        role = "status"
        span("Loading...", className = "visually-hidden")
    }