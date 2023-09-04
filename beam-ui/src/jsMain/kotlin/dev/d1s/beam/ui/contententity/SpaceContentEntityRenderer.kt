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

package dev.d1s.beam.ui.contententity

import dev.d1s.beam.client.PublicBeamClient
import dev.d1s.beam.commons.Block
import dev.d1s.beam.commons.BlockSize
import dev.d1s.beam.commons.contententity.*
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.component.SpaceCardComponent
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.render
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

class SpaceContentEntityRenderer : ContentEntityRenderer, KoinComponent {

    override val definition = SpaceContentEntityTypeDefinition

    private val client by inject<PublicBeamClient>()

    private val renderingScope = CoroutineScope(Dispatchers.Main)

    override fun SimplePanel.render(sequence: ContentEntities, block: Block) {
        val lgCols = if (block.size >= BlockSize.LARGE) 2 else 1
        val isLast = sequence.isLastIn(block)

        renderRow(lgCols, isLast) {
            renderSequence(sequence)
        }
    }

    private fun SimplePanel.renderRow(lgCols: Int, last: Boolean, block: SimplePanel.() -> Unit) {
        div(className = "w-100 row row-cols-1 row-cols-lg-$lgCols g-3") {
            if (!last) {
                addCssClass("mb-3")
            }

            block()
        }
    }

    private fun SimplePanel.renderSequence(sequence: ContentEntities) {
        sequence.forEach { entity ->
            asyncDiv {
                renderSpaceCard(entity)
            }
        }
    }

    private fun SimplePanel.asyncDiv(block: suspend SimplePanel.() -> Unit) {
        div {
            renderingScope.launch {
                block()
            }
        }
    }

    private suspend fun SimplePanel.renderSpaceCard(entity: ContentEntity) {
        val spaceIdentifier = entity.parameters[definition.identifier]
        requireNotNull(spaceIdentifier)

        val space = client.getSpace(spaceIdentifier).getOrNull()

        val spaceCard = get<Component<SpaceCardComponent.Config>>(Qualifier.SpaceCardComponent)

        render(spaceCard) {
            this.space.value = space
        }
    }
}