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

package dev.d1s.beam.commons.validation

import dev.d1s.beam.commons.ModifiedRootSpace
import dev.d1s.beam.commons.ModifiedSpace
import dev.d1s.beam.commons.Regex
import dev.d1s.beam.commons.SpaceSlug
import dev.d1s.exkt.konform.matches
import io.konform.validation.Validation

public val validateRootSpace: Validation<ModifiedRootSpace> = Validation {
    ModifiedRootSpace::view {
        run(validateSpaceView)
    }
}

public val validateSpace: Validation<ModifiedSpace> = Validation {
    ModifiedSpace::slug {
        val spaceSlugNotInBlacklist: Validation<SpaceSlug> = Validation {
            addConstraint("space slug is blacklisted") { slug ->
                !SpaceSlugBlacklist.matches(slug)
            }
        }

        run(spaceSlugNotInBlacklist)
        matches(Regex.Slug) hint "space slug must match ${Regex.Slug}"
    }

    ModifiedSpace::metadata {
        run(validateMetadata)
    }

    ModifiedSpace::view {
        run(validateSpaceView)
    }
}