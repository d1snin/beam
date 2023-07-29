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

import dev.d1s.beam.commons.Space
import dev.d1s.beam.commons.SpaceIdentifier
import io.ktor.http.*
import dev.d1s.beam.commons.Url as UrlString

public interface SpaceResolver {

    public suspend fun resolve(url: UrlString): Result<Space>

    public fun resolveIdentifier(url: UrlString): SpaceIdentifier?
}

public class DefaultSpaceResolver(private val client: PublicBeamClient) : SpaceResolver {

    override suspend fun resolve(url: UrlString): Result<Space> =
        runCatching {
            val identifier = resolveIdentifier(url) ?: error("Couldn't resolve identifier")

            client.getSpace(identifier).getOrThrow()
        }

    override fun resolveIdentifier(url: UrlString): SpaceIdentifier? {
        val segments = Url(url).pathSegments

        return when (segments.size) {
            0 -> "root"
            1 -> segments.first()
            else -> null
        }
    }
}