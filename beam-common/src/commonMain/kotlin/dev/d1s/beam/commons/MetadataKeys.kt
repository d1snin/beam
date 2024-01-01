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
    public const val UI_BLOCK_BUTTON_LINK_ENTITY_GROW: MetadataKey = "ui.block.button-link.grow"

    public const val FILE_CONTENT_ENTITY_SIZE: MetadataKey = "file.size"
}

public val Metadata.spaceKeywords: MetadataValue?
    get() = get(MetadataKeys.BUNDLE_SPACE_KEYWORDS)

public val Metadata?.spaceShowStatus: Boolean
    get() = getBooleanOrTrue(MetadataKeys.UI_SPACE_SHOW_STATUS)

public val Metadata.spaceBackground: MetadataValue?
    get() = get(MetadataKeys.UI_SPACE_BACKGROUND)

public val Metadata?.spaceBackgroundFixed: Boolean
    get() = getBooleanOrFalse(MetadataKeys.UI_SPACE_BACKGROUND_FIXED)

public val Metadata?.spaceStretchLastBlocks: Boolean
    get() = getBooleanOrFalse(MetadataKeys.UI_SPACE_STRETCH_LAST_BLOCKS)

public val Metadata?.rowStretch: Boolean
    get() = getBooleanOrTrue(MetadataKeys.UI_ROW_STRETCH)

public val Metadata.blockId: MetadataValue?
    get() = get(MetadataKeys.UI_BLOCK_ID)

public val Metadata?.blockBare: Boolean
    get() = getBooleanOrFalse(MetadataKeys.UI_BLOCK_BARE)

public val Metadata.blockLink: MetadataValue?
    get() = get(MetadataKeys.UI_BLOCK_LINK)

public val Metadata?.blockImageEntityFluid: Boolean
    get() = getBooleanOrFalse(MetadataKeys.UI_BLOCK_IMAGE_ENTITY_FLUID)

public val Metadata?.blockButtonLinkEntityGrow: Boolean
    get() = getBooleanOrFalse(MetadataKeys.UI_BLOCK_BUTTON_LINK_ENTITY_GROW)

public val Metadata.fileContentEntitySize: Long?
    get() = get(MetadataKeys.FILE_CONTENT_ENTITY_SIZE)?.toLongOrNull()

private fun Metadata?.getBooleanOrTrue(key: MetadataKey) =
    this?.get(key)?.toBooleanStrictOrNull() ?: true

private fun Metadata?.getBooleanOrFalse(key: MetadataKey) =
    this?.get(key)?.toBooleanStrictOrNull() ?: false