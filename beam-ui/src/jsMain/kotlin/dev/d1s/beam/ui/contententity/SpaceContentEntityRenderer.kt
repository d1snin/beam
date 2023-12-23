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

import dev.d1s.beam.commons.contententity.SpaceContentEntityTypeDefinition
import dev.d1s.beam.commons.contententity.get
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.component.SpaceCardComponent
import dev.d1s.exkt.kvision.bootstrap.w100
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.render
import io.kvision.html.div
import io.kvision.panel.SimplePanel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class SpaceContentEntityRenderer : SingleContentEntityRenderer, KoinComponent {

    override val definition = SpaceContentEntityTypeDefinition

    override fun SimplePanel.render(context: SingleContentEntityRenderingContext) {
        div {
            w100()

            renderSpaceCard(context)
            separateContentEntity(context)
        }
    }

    private fun SimplePanel.renderSpaceCard(
        context: SingleContentEntityRenderingContext
    ) {
        val entity = context.entity

        val spaceIdentifier = entity.parameters[definition.identifier]
        requireNotNull(spaceIdentifier)

        val spaceCard = get<Component<SpaceCardComponent.Config>>(Qualifier.SpaceCardComponent)

        render(spaceCard) {
            this.spaceIdentifier.value = spaceIdentifier
            this.fullWidth.value = true
        }
    }
}