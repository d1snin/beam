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

import dev.d1s.beam.commons.contententity.ContentEntities
import dev.d1s.beam.commons.contententity.ContentEntity
import io.kvision.panel.SimplePanel
import org.koin.core.context.GlobalContext

private val contentEntityRenderers by lazy {
    GlobalContext.get().getAll<ContentEntityRenderer>()
}

fun SimplePanel.renderEntities(entities: ContentEntities) {
    val sequence = mutableListOf<ContentEntity>()
    fun lastEntity() = sequence.lastOrNull()

    fun render() {
        val lastEntity = lastEntity()

        lastEntity?.let {
            val renderer = getRenderer(entity = it)

            with(renderer) {
                render(sequence)
            }

            sequence.clear()
        }
    }

    entities.forEach { entity ->
        val lastEntity = lastEntity()

        when {
            lastEntity == null -> {
                sequence.add(entity)
            }

            entity.type == lastEntity.type -> {
                sequence.add(entity)
            }

            else -> {
                render()
            }
        }
    }

    render()
}

private fun getRenderer(entity: ContentEntity): ContentEntityRenderer {
    val type = entity.type

    val renderer = contentEntityRenderers.find {
        it.definition.name == type
    }

    return renderer ?: error("No renderer for type $type")
}