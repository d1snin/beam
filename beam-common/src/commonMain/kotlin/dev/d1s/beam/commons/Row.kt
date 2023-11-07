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

package dev.d1s.beam.commons

import kotlinx.serialization.Serializable

public typealias RowQualifier = String

public typealias RowIndex = Int

public fun RowQualifier(spaceId: SpaceId?, index: RowIndex): RowQualifier =
    (spaceId?.let { "$it-" } ?: "") + index.toString()

public sealed interface AbstractRow {

    public val align: RowAlign
}

@Serializable
public data class Row(
    val index: RowIndex,
    override val align: RowAlign,
    val spaceId: SpaceId
) : AbstractRow

@Serializable
public data class RowModification(
    override val align: RowAlign
) : AbstractRow

public enum class RowAlign {
    START, END, CENTER, BETWEEN
}