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

import dev.d1s.exkt.kvision.bootstrap.bootstrapIcon
import dev.d1s.exkt.kvision.bootstrap.fs1
import dev.d1s.exkt.kvision.bootstrap.me4
import io.kvision.html.*
import io.kvision.panel.SimplePanel

fun SimplePanel.renderContainer(block: SimplePanel.() -> Unit = {}) =
    div(className = "container", init = block)

fun SimplePanel.renderFluidContainer(block: SimplePanel.() -> Unit = {}) =
    div(className = "container-fluid", init = block)

fun SimplePanel.renderOffCanvas(block: SimplePanel.() -> Unit = {}) =
    div(className = "offcanvas offcanvas-start") {
        tabindex = -1

        block()
    }

fun SimplePanel.renderOffCanvasBody(block: SimplePanel.() -> Unit) {
    div(className = "offcanvas-body", init = block)
}

fun SimplePanel.renderDropdownToggler(style: ButtonStyle, offset: String, block: SimplePanel.() -> Unit = {}) =
    button(text = "", style = style, className = "btn-sm dropdown-toggle") {
        setAttribute("data-bs-toggle", "dropdown")
        setAttribute("data-bs-auto-close", "outside")
        setAttribute("data-bs-offset", offset)
        setAttribute("aria-expanded", "false")

        block()
    }

fun SimplePanel.renderOffCanvasToggle(target: String, block: SimplePanel.() -> Unit = {}) =
    renderUnstyledLink(url = "#$target") {
        fs1()
        me4()

        role = "button"
        setAttribute("data-bs-toggle", "offcanvas")

        bootstrapIcon(Icons.LIST)

        block()
    }

fun SimplePanel.renderDropup(block: SimplePanel.() -> Unit = {}) =
    div(className = "dropup", init = block)

fun SimplePanel.renderDropdownMenu(block: SimplePanel.() -> Unit = {}) =
    div(className = "dropdown-menu", init = block)

fun SimplePanel.renderCollapsedContainer(block: SimplePanel.() -> Unit = {}) =
    div(className = "collapse", init = block)

fun SimplePanel.renderDropdownItem(block: SimplePanel. () -> Unit) {
    li {
        tag(TAG.BUTTON, className = "dropdown-item", init = block)
    }
}

fun SimplePanel.renderRow(cols: String, lgCols: String? = null, gap: Int? = null, block: SimplePanel.() -> Unit = {}) =
    div(
        className = "row row-cols-$cols" +
                (lgCols?.let { " row-cols-lg-$it" } ?: "") +
                (gap?.let { " g-$it" } ?: ""),
        init = block
    )

fun SimplePanel.renderCol(block: SimplePanel.() -> Unit = {}) =
    div(className = "col", init = block)

fun SimplePanel.renderCard(block: SimplePanel.() -> Unit = {}) =
    div(className = "card", init = block)

fun SimplePanel.renderCardBody(block: SimplePanel.() -> Unit = {}) =
    div(className = "card-body", init = block)

fun SimplePanel.renderExternalButtonLink(
    url: String,
    style: dev.d1s.beam.commons.contententity.ButtonStyle,
    block: SimplePanel.() -> Unit = {}
) =
    renderFriendlyLink(
        url = url,
        className = "btn btn-outline-${style.code}",
        external = true
    ) {
        role = "button"

        block()
    }