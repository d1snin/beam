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

package dev.d1s.beam.ui.contententity

import dev.d1s.beam.commons.Block
import dev.d1s.beam.commons.contententity.*
import io.kvision.panel.SimplePanel
import org.koin.core.context.GlobalContext

private typealias MutableContentEntitySequence = MutableList<ContentEntity>

private val contentEntityRenderers by lazy {
    GlobalContext.get().getAll<ContentEntityRenderer>()
}

private val ContentEntityParameters.alignment
    get() = this[CommonParameters.ALIGNMENT]?.let { Alignment.byName(it) } ?: Alignment.Default

fun SimplePanel.renderEntities(block: Block, batch: ContentEntities) {
    val sequence = mutableListOf<ContentEntity>()

    batch.forEach { entity ->
        processEntity(entity, sequence, batch, block)
    }

    // finalize
    processSequence(sequence, batch, block)
}

private fun SimplePanel.processEntity(
    entity: ContentEntity,
    sequence: MutableContentEntitySequence,
    batch: ContentEntities,
    block: Block
) {
    val lastEntity = sequence.lastOrNull()

    when {
        lastEntity == null || entity.type == lastEntity.type -> {
            sequence.add(entity)
        }

        else -> {
            processSequence(sequence, batch, block)

            sequence.add(entity)
        }
    }
}

private fun SimplePanel.processSequence(sequence: MutableContentEntitySequence, batch: ContentEntities, block: Block) {
    if (sequence.isNotEmpty()) {
        sequence.splitBy(selector = { it.parameters.alignment }) { alignedSequence, alignment ->
            renderSequence(alignedSequence, batch, block, alignment)
        }

        sequence.clear()
    }
}

private fun SimplePanel.renderSequence(
    sequence: ContentEntities,
    batch: ContentEntities,
    block: Block,
    alignment: Alignment
) {
    val lastEntity = sequence.last()
    val renderer = getRenderer(entity = lastEntity)

    with(renderer) {
        val context = SequenceContentEntityRenderingContext(sequence, batch, block, alignment)
        render(context)
    }
}

private fun getRenderer(entity: ContentEntity): ContentEntityRenderer {
    val type = entity.type

    val renderer = contentEntityRenderers.find {
        it.definition.name == type
    }

    return renderer ?: error("No renderer for type $type")
}