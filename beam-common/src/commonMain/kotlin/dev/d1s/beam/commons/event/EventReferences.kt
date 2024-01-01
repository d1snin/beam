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

package dev.d1s.beam.commons.event

import dev.d1s.beam.commons.*
import dev.d1s.ktor.events.commons.EventReference
import dev.d1s.ktor.events.commons.ref

public object EventReferences {

    public val spaceCreated: EventReference = ref("space-created")

    public val blockCreated: EventReference = ref("block-created")

    public val rowCreated: EventReference = ref("row-created")

    public val translationCreated: EventReference = ref("translation-created")

    public fun spaceUpdated(id: SpaceId? = null): EventReference = ref("space-updated", id)

    public fun spaceRemoved(id: SpaceId? = null): EventReference = ref("space-removed", id)

    public fun blockUpdated(id: BlockId? = null): EventReference = ref("block-updated", id)

    public fun blockRemoved(id: BlockId? = null): EventReference = ref("block-removed", id)

    public fun rowUpdated(qualifier: RowQualifier? = null): EventReference = ref("row-updated", qualifier)

    public fun translationUpdated(qualifier: TranslationQualifier? = null): EventReference =
        ref("translation-updated", qualifier)

    public fun translationRemoved(qualifier: TranslationQualifier? = null): EventReference =
        ref("translation-removed", qualifier)
}