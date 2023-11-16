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
import dev.d1s.beam.commons.Space
import dev.d1s.beam.commons.SpaceIdentifier
import dev.d1s.beam.commons.contententity.SpaceContentEntityTypeDefinition
import dev.d1s.beam.commons.contententity.get
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.component.SpaceCardComponent
import dev.d1s.beam.ui.util.currentLanguageCode
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.render
import io.ktor.util.collections.*
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

class SpaceContentEntityRenderer : SingleContentEntityRenderer, KoinComponent {

    override val definition = SpaceContentEntityTypeDefinition

    private val client by inject<BeamClient>()

    private val renderingScope = CoroutineScope(Dispatchers.Main)

    private val spaceCache = ConcurrentMap<SpaceIdentifier, Space?>()

    override fun SimplePanel.render(context: SingleContentEntityRenderingContext) {
        asyncDiv {
            renderSpaceCard(context)
            separateContentEntity(context)
        }
    }

    private fun SimplePanel.asyncDiv(block: suspend SimplePanel.() -> Unit) {
        div(className = "w-100") {
            renderingScope.launch {
                block()
            }
        }
    }

    private suspend fun SimplePanel.renderSpaceCard(
        context: SingleContentEntityRenderingContext
    ) {
        val entity = context.entity

        val spaceIdentifier = entity.parameters[definition.identifier]
        requireNotNull(spaceIdentifier)

        val spaceCard = get<Component<SpaceCardComponent.Config>>(Qualifier.SpaceCardComponent)

        val space = spaceCache.getOrPut(spaceIdentifier) {
            client.getSpace(spaceIdentifier, currentLanguageCode).getOrNull()
        }

        render(spaceCard) {
            this.space.value = space
            this.cardFullWidth.value = true
        }
    }
}