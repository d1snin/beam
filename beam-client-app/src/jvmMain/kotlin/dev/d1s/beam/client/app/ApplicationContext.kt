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
import dev.d1s.beam.client.Void
import dev.d1s.beam.client.app.state.BlockContext
import dev.d1s.beam.commons.BlockSize
import dev.d1s.beam.commons.toSpace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import org.lighthousegames.logging.logging

public class ApplicationContext internal constructor(
    private val config: ApplicationConfig,
    private val client: BeamClient,
    private val scope: CoroutineScope
) : BeamClient by client {

    private val log = logging()

    private val applicationContextInitialized = Mutex(locked = true)

    init {
        scope.launch {
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

            val spaceId = space.id

            log.i {
                "Accessed space `${space.slug}` ($spaceId). Cleaning it up..."
            }

            val blocks = getBlocks(space.id).getOrThrow()
            blocks.forEach {
                deleteBlock(it.id).getOrElse {
                    unableToModify()
                }
            }

            applicationContextInitialized.unlock()
        }
    }

    public suspend fun block(configure: BlockContext.() -> Unit) {
        applicationContextInitialized.lock()

        val space = config.space

        val createdBlock = postBlock {
            index = getBlocks(space).getOrThrow().size
            size = BlockSize.SMALL
            spaceId = space

            entity {
                type = Void
            }
        }.getOrThrow()

        val context = BlockContext(
            createdBlock,
            client,
            scope
        )

        scope.launch {
            context.configure()
        }
    }

    private fun unableToModify(): Nothing =
        error("Unable to modify space.")

    private companion object {

        private const val DEFAULT_THEME = "catppuccin-mocha"
    }
}