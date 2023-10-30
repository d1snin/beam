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

import dev.d1s.beam.commons.Block
import dev.d1s.beam.commons.contententity.Alignment
import dev.d1s.beam.commons.contententity.ContentEntities
import dev.d1s.beam.commons.contententity.ContentEntity
import dev.d1s.beam.commons.contententity.isFirstIn

interface ContentEntityRenderingContext {

    val batch: ContentEntities

    val block: Block

    val alignment: Alignment
}

data class SingleContentEntityRenderingContext(
    val entity: ContentEntity,
    override val batch: ContentEntities,
    override val block: Block,
    override val alignment: Alignment
) : ContentEntityRenderingContext {

    fun isFirst() = entity.isFirstIn(batch)
}

data class SequenceContentEntityRenderingContext(
    val sequence: ContentEntities,
    override val batch: ContentEntities,
    override val block: Block,
    override val alignment: Alignment
) : ContentEntityRenderingContext