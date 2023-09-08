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
import dev.d1s.beam.commons.contententity.ContentEntities
import dev.d1s.beam.commons.contententity.ContentEntity
import dev.d1s.beam.commons.contententity.isFirstIn
import dev.d1s.beam.commons.contententity.isLastIn
import io.kvision.panel.SimplePanel

fun SimplePanel.separateContentEntity(
    contentEntity: ContentEntity,
    block: Block,
    topMargin: Int? = null,
    bottomMargin: Int = 3
) {
    val first = contentEntity.isFirstIn(block)
    val last = contentEntity.isLastIn(block)

    val topMarginLevel = topMargin?.takeIf { !first } ?: 0
    val bottomMarginLevel = bottomMargin.takeIf { !last } ?: 0

    addCssClass("mt-$topMarginLevel")
    addCssClass("mb-$bottomMarginLevel")
}

fun SimplePanel.separateContentEntities(
    contentEntities: ContentEntities,
    block: Block,
    topMargin: Int? = null,
    bottomMargin: Int = 3
) {
    separateContentEntity(contentEntities.last(), block, topMargin, bottomMargin)
}