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

package dev.d1s.beam.client

import dev.d1s.beam.commons.MetadataKey
import dev.d1s.beam.commons.MetadataValue
import dev.d1s.beam.commons.RowAlign
import dev.d1s.beam.commons.RowModification

@BuilderDsl
public class RowModificationBuilder {

    public var align: RowAlign? = null

    private val metadataBuilder = MetadataBuilder()

    public fun metadata(key: MetadataKey, value: MetadataValue) {
        metadataBuilder.metadata(key, value)
    }

    public fun metadata(build: MetadataBuilder.() -> Unit) {
        metadataBuilder.apply(build)
    }

    public fun buildRowModification(): RowModification =
        RowModification(
            align ?: error("Row align is undefined"),
            metadata = metadataBuilder.buildMetadata()
        )
}