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

import dev.d1s.beam.client.BeamClient
import dev.d1s.beam.commons.Block
import dev.d1s.beam.commons.Blocks
import dev.d1s.beam.commons.Space
import dev.d1s.beam.ui.theme.setTextColor
import io.kvision.panel.SimplePanel
import kotlinx.browser.window
import org.koin.core.context.GlobalContext

private val client by lazy {
    GlobalContext.get().get<BeamClient>()
}

private val resolver by lazy {
    client.resolver
}

val currentSpaceIdentifier
    get() = resolver.resolveIdentifier(window.location.href)

private var lateInitCurrentSpace: Space? = null
val currentSpace get() = lateInitCurrentSpace

val currentSpaceUrl get() = buildSpaceUrl(currentSpace?.slug)

private var internalCurrentBlocks: MutableList<Block> = mutableListOf()
val currentBlocks: Blocks = internalCurrentBlocks

suspend fun initCurrentSpace() {
    lateInitCurrentSpace = resolver.resolve(window.location.href, currentLanguageCode).getOrNull()

    internalCurrentBlocks.clear()
    internalCurrentBlocks += currentSpace?.let {
        client.getBlocks(it.id, currentLanguageCode).getOrNull()
    } ?: listOf()
}

fun setCurrentSpaceBlocks(blocks: Blocks) {
    internalCurrentBlocks.clear()
    internalCurrentBlocks += blocks
}

fun SimplePanel.renderSpaceLink(space: Space? = null, block: SimplePanel.() -> Unit) {
    val url = space?.let { buildSpaceUrl(it.slug) } ?: currentSpaceUrl

    renderFriendlyLink(url = url, className = "text-decoration-none") {
        setTextColor()
        block()
    }
}