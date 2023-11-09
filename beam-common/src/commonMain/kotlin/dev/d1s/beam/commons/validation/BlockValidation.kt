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

package dev.d1s.beam.commons.validation

import dev.d1s.beam.commons.AbstractBlock
import dev.d1s.beam.commons.BlockIndex
import io.konform.validation.Validation
import io.konform.validation.ValidationBuilder
import io.konform.validation.jsonschema.maxItems
import io.konform.validation.jsonschema.minItems
import io.konform.validation.jsonschema.minimum

public val validateBlock: Validation<AbstractBlock> = Validation {
    AbstractBlock::row {
        requireValidRowIndex()
    }

    AbstractBlock::index ifPresent {
        requireValidBlockIndex()
    }

    AbstractBlock::entities {
        minItems(1) hint "block must have at least 1 entity"
        maxItems(Limits.BLOCK_MAX_CAPACITY) hint "block must have less than ${Limits.BLOCK_MAX_CAPACITY} entities"
    }

    AbstractBlock::entities onEach {
        run(validateContentEntity)
    }

    AbstractBlock::metadata {
        run(validateMetadata)
    }
}

private fun ValidationBuilder<BlockIndex>.requireValidBlockIndex() {
    minimum(0) hint "block index must be greater or equal to 0"
}