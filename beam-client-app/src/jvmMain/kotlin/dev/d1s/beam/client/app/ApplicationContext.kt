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

package dev.d1s.beam.client.app

import dev.d1s.beam.client.BeamClient
import dev.d1s.beam.commons.Space
import dev.d1s.beam.commons.toSpace
import kotlinx.coroutines.*
import org.lighthousegames.logging.logging

public class ApplicationContext internal constructor(
    internal val config: ApplicationConfig,
    private val client: BeamClient
) : BeamClient by client {

    internal var internalSpace: Space? = null

    public val space: Space
        get() = internalSpace ?: error("Space is not yet initialized.")

    private val contextJobs = mutableListOf<Job>()

    private val scope = CoroutineScope(Dispatchers.IO)

    private val log = logging()

    init {
        contextJobs += scope.launch {
            initSpace()
        }
    }

    internal suspend fun waitContext() {
        contextJobs.joinAll()
    }

    private suspend fun initSpace() {
        log.d {
            "Initializing application context..."
        }

        val spaceIdentifier = config.space
        var space = getSpace(spaceIdentifier).getOrNull()

        if (space == null) {
            log.i {
                "Couldn't access space '$spaceIdentifier'. Creating it..."
            }

            space = postSpace {
                slug = spaceIdentifier
                view {
                    theme = DEFAULT_THEME
                }
            }.getOrThrow().toSpace()
        }

        internalSpace = space

        val spaceId = space.id

        log.i {
            "Accessed space `${space.slug}` ($spaceId). Cleaning it up..."
        }

        val blocks = getBlocks(space.id).getOrThrow()
        blocks.forEach {
            deleteBlock(it.id).getOrThrow()
        }

        log.i {
            "Space initialized."
        }
    }

    private companion object {

        private const val DEFAULT_THEME = "catppuccin-mocha"
    }
}