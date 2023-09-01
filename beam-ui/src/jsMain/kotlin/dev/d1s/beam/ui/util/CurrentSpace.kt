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

package dev.d1s.beam.ui.util

import dev.d1s.beam.client.PublicBeamClient
import dev.d1s.beam.commons.Blocks
import dev.d1s.beam.commons.Space
import dev.d1s.beam.commons.SpaceIdentifier
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.client.DaemonStatusWithPing
import dev.d1s.beam.ui.state.Observable
import dev.d1s.beam.ui.theme.setTextColor
import io.kvision.html.div
import io.kvision.html.link
import io.kvision.panel.SimplePanel
import io.kvision.state.bind
import kotlinx.browser.window
import org.koin.core.context.GlobalContext

private val client by lazy {
    GlobalContext.get().get<PublicBeamClient>()
}

private val resolver by lazy {
    client.resolver
}

private val daemonStatusObservable by lazy {
    GlobalContext.get().get<Observable<DaemonStatusWithPing?>>(Qualifier.DaemonStatusObservable)
}

private var lateInitCurrentSpaceIdentifier: SpaceIdentifier? = null
val currentSpaceIdentifier: SpaceIdentifier? get() = lateInitCurrentSpaceIdentifier

private var lateInitCurrentSpace: Space? = null
val currentSpace get() = lateInitCurrentSpace

val currentSpaceUrl get() = buildSpaceUrl(currentSpace?.slug)

private var lateInitCurrentBlocks: Blocks? = null
val currentBlocks get() = lateInitCurrentBlocks

fun initCurrentSpaceIdentifier() {
    lateInitCurrentSpaceIdentifier = resolver.resolveIdentifier(window.location.href)
}

suspend fun initCurrentSpace() {
    initCurrentSpaceIdentifier()

    lateInitCurrentSpace = resolver.resolve(window.location.href).getOrNull()
    lateInitCurrentBlocks = currentSpace?.let {
        client.getBlocks(it.id).getOrNull()
    }
}

fun setCurrentSpaceIdentifier(identifier: SpaceIdentifier) {
    lateInitCurrentSpaceIdentifier = identifier

    window.location.href = buildSpaceUrl(identifier)
}

fun setCurrentSpace(space: Space?) {
    lateInitCurrentSpace = space
}

fun setCurrentSpaceBlocks(blocks: Blocks?) {
    lateInitCurrentBlocks = blocks
}

fun SimplePanel.renderSpaceLink(space: Space? = null, block: SimplePanel.() -> Unit) {
    div {
        bind(daemonStatusObservable.state) { status ->
            if (status != null) {
                link(
                    label = "",
                    space?.let { buildSpaceUrl(it.slug) } ?: currentSpaceUrl,
                    className = "text-decoration-none") {
                    setTextColor()
                    block()
                }
            } else {
                div {
                    block()
                }
            }
        }
    }
}