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

package dev.d1s.beam.daemon.service.contententity

import dev.d1s.beam.commons.MetadataKeys
import dev.d1s.beam.commons.MutableMetadata
import dev.d1s.beam.commons.contententity.ContentEntity
import dev.d1s.beam.commons.contententity.File
import dev.d1s.beam.commons.contententity.get
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging

class FileContentEntityMetadataProcessor : ContentEntityMetadataProcessor, KoinComponent {

    override val type = File

    private val httpClient by inject<HttpClient>()

    private val log = logging()

    override suspend fun populate(entity: ContentEntity, metadata: MutableMetadata) {
        val url = entity.parameters[File.url]
        requireNotNull(url)

        log.d {
            "File url: $url"
        }

        val contentLength = runCatching {
            httpClient.head(url).contentLength()
        }.onFailure {
            it.printStackTrace()
        }.getOrNull()

        log.d {
            "Content length: $contentLength"
        }

        contentLength?.let {
            metadata[MetadataKeys.FILE_CONTENT_ENTITY_SIZE] = it.toString()
        }
    }
}