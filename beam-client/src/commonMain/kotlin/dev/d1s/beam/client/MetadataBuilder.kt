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

package dev.d1s.beam.client

import dev.d1s.beam.commons.*

@BuilderDsl
public class MetadataBuilder {

    private val metadata = metadataOf().toMutableMap()

    public fun metadata(key: MetadataKey, value: MetadataValue) {
        metadata[key] = value
    }

    public fun buildMetadata(): Metadata = metadata
}

public fun MetadataBuilder.setSpaceKeywords(keywords: String) {
    metadata(MetadataKeys.BUNDLE_SPACE_KEYWORDS, keywords)
}

public fun MetadataBuilder.setSpaceShowStatus(show: Boolean = true) {
    metadata(MetadataKeys.UI_SPACE_SHOW_STATUS, show.toString())
}

public fun MetadataBuilder.setBlockId(id: String) {
    metadata(MetadataKeys.UI_BLOCK_ID, id)
}

public fun MetadataBuilder.setBlockBare(bare: Boolean = true) {
    metadata(MetadataKeys.UI_BLOCK_BARE, bare.toString())
}

public fun MetadataBuilder.setBlockLink(link: String) {
    metadata(MetadataKeys.UI_BLOCK_LINK, link)
}

public fun MetadataBuilder.setBlockImageEntityFluid(fluid: Boolean = true) {
    metadata(MetadataKeys.UI_BLOCK_IMAGE_ENTITY_FLUID, fluid.toString())
}