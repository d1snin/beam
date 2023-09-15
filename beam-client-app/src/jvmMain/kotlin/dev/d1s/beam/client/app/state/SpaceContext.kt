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

package dev.d1s.beam.client.app.state

import dev.d1s.beam.client.ViewConfigurationBuilder
import dev.d1s.beam.client.app.ApplicationContext
import dev.d1s.beam.commons.Metadata
import dev.d1s.beam.commons.SpaceModification
import dev.d1s.beam.commons.SpaceSlug
import dev.d1s.beam.commons.ViewConfiguration
import kotlinx.coroutines.sync.Mutex
import org.lighthousegames.logging.logging

public class SpaceContext internal constructor(
    private val app: ApplicationContext
) {
    private val space get() = app.space

    private val operationLock = Mutex()

    private val log = logging()

    public suspend fun setSlug(slug: suspend () -> SpaceSlug) {
        modifySpace(slug = slug())
    }

    public suspend fun setMetadata(metadata: suspend () -> Metadata) {
        modifySpace(metadata = metadata())
    }

    public suspend fun setView(view: suspend ViewConfigurationBuilder.() -> Unit) {
        val builtView = ViewConfigurationBuilder().apply { view() }.build()
        modifySpace(view = builtView)
    }

    private suspend fun modifySpace(
        slug: SpaceSlug = space.slug,
        metadata: Metadata = space.metadata,
        view: ViewConfiguration = space.view
    ) {
        operationLock.lock()

        try {
            val modification = SpaceModification(slug, metadata, view)

            log.d {
                "Modifying space with the following data: $modification"
            }

            val modifiedSpace = app.putSpace(space.id, modification).getOrThrow()
            app.internalSpace = modifiedSpace
        } finally {
            operationLock.unlock()
        }
    }
}

public suspend fun ApplicationContext.space(configure: suspend SpaceContext.() -> Unit) {
    launchJob {
        val context = SpaceContext(app = this@space)

        context.configure()
    }
}