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

import dev.d1s.beam.client.BeamClient
import dev.d1s.beam.commons.BlockSize
import dev.d1s.beam.commons.contententity.ContentEntities
import dev.d1s.beam.commons.contententity.ContentEntity
import dev.d1s.beam.commons.contententity.SpaceContentEntityTypeDefinition
import dev.d1s.beam.commons.contententity.get
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.component.SpaceCardComponent
import dev.d1s.beam.ui.util.currentLanguageCode
import dev.d1s.beam.ui.util.justifyContent
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

    private val client by inject<BeamClient>()

    private val renderingScope = CoroutineScope(Dispatchers.Main)

    private val ContentEntity.fullWidth
        get() = parameters[definition.fullWidth]?.toBooleanStrict() ?: false

    override fun SimplePanel.render(context: SequenceContentEntityRenderingContext) {
        val lgCols = if (context.block.size >= BlockSize.LARGE) 2 else 1

        context.sequence.splitBy(selector = { it.fullWidth }) { entities, fullWidth ->
            fun SimplePanel.renderSequence() {
                renderSequence(entities, context, fullWidth)
            }

            if (fullWidth) {
                renderSequence()
            } else {
                renderRow(lgCols, context) {
                    renderSequence()
                    separateContentEntities(context)
                }
            }
        }
    }

    private fun SimplePanel.renderRow(
        lgCols: Int,
        context: SequenceContentEntityRenderingContext,
        block: SimplePanel.() -> Unit
    ) {
        div(className = "w-100 row row-cols-1 row-cols-lg-$lgCols") {
            justifyContent(context.alignment)

            block()
        }
    }

    private fun SimplePanel.renderSequence(
        entities: ContentEntities,
        context: SequenceContentEntityRenderingContext,
        fullWidth: Boolean
    ) {
        entities.forEach { entity ->
            asyncDiv {
                renderSpaceCard(entity, context, fullWidth)
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

    private suspend fun SimplePanel.renderSpaceCard(
        entity: ContentEntity,
        context: SequenceContentEntityRenderingContext,
        fullWidth: Boolean
    ) {
        val spaceIdentifier = entity.parameters[definition.identifier]
        requireNotNull(spaceIdentifier)

        val space = client.getSpace(spaceIdentifier, currentLanguageCode).getOrNull()

        val spaceCard = get<Component<SpaceCardComponent.Config>>(Qualifier.SpaceCardComponent)

        if (fullWidth) {
            separateContentEntity(entity, context)
        }

        render(spaceCard) {
            this.space.value = space
            this.cardFullWidth.value = fullWidth
        }
    }
}