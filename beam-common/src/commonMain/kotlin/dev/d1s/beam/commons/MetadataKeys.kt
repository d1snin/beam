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

public object MetadataKeys {

    public const val BUNDLE_SPACE_KEYWORDS: MetadataKey = "bundle.space.keywords"

    public const val UI_SPACE_SHOW_STATUS: MetadataKey = "ui.space.show-status"
    public const val UI_SPACE_BACKGROUND: MetadataKey = "ui.space.background"
    public const val UI_SPACE_BACKGROUND_FIXED: MetadataKey = "ui.space.background.fixed"
    public const val UI_SPACE_STRETCH_LAST_BLOCKS: MetadataKey = "ui.space.stretch-last-blocks"

    public const val UI_ROW_STRETCH: MetadataKey = "ui.row.stretch"

    public const val UI_BLOCK_ID: MetadataKey = "ui.block.id"
    public const val UI_BLOCK_BARE: MetadataKey = "ui.block.bare"
    public const val UI_BLOCK_LINK: MetadataKey = "ui.block.link"
    public const val UI_BLOCK_IMAGE_ENTITY_FLUID: MetadataKey = "ui.block.image.fluid"

    public const val FILE_CONTENT_ENTITY_SIZE: MetadataKey = "file.size"
}