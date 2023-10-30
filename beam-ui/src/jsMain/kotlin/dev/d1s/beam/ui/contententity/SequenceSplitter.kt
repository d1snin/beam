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

fun <T> ContentEntities.splitBy(selector: (ContentEntity) -> T, block: (ContentEntities, T) -> Unit) {
    val entities = this
    val buffer = mutableListOf<ContentEntity>()

    var bufferProperty = selector(entities.first())

    fun executeBuffer() {
        block(buffer, bufferProperty)
        buffer.clear()
    }

    entities.forEach { entity ->
        val entityProperty = selector(entity)

        if (bufferProperty == entityProperty) {
            buffer.add(entity)
        } else {
            executeBuffer()
            bufferProperty = entityProperty
            buffer.add(entity)
        }
    }

    executeBuffer()
}