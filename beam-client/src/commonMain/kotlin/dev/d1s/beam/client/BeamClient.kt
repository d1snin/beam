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

import dev.d1s.beam.client.response.Spaces
import dev.d1s.beam.commons.*
import dev.d1s.beam.commons.event.EntityUpdate
import dev.d1s.beam.commons.event.EventReferences
import dev.d1s.exkt.common.pagination.LimitAndOffset
import dev.d1s.exkt.common.replaceIdPlaceholder
import dev.d1s.ktor.events.client.ClientWebSocketEvent
import dev.d1s.ktor.events.client.WebSocketEvents
import dev.d1s.ktor.events.client.receiveWebSocketEvent
import dev.d1s.ktor.events.client.webSocketEvents
import dev.d1s.ktor.events.commons.EventReference
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

public typealias BeamDaemonBaseUrl = String

public interface PublicBeamClient {

    public val httpBaseUrl: BeamDaemonBaseUrl

    public val wsBaseUrl: BeamDaemonBaseUrl?

    public val resolver: SpaceResolver

    public suspend fun getDaemonStatus(): Result<DaemonStatus>

    public suspend fun postSpace(space: SpaceModification): Result<SpaceWithToken>

    public suspend fun postSpace(build: SpaceModificationBuilder.() -> Unit): Result<SpaceWithToken>

    public suspend fun postRootSpace(space: RootSpaceModification): Result<SpaceWithToken>

    public suspend fun postRootSpace(build: RootSpaceModificationBuilder.() -> Unit): Result<SpaceWithToken>

    public suspend fun getSpace(id: SpaceIdentifier, languageCode: LanguageCode? = null): Result<Space>

    public suspend fun getSpaces(limitAndOffset: LimitAndOffset, languageCode: LanguageCode? = null): Result<Spaces>

    public suspend fun getSpaces(limit: Int, offset: Int, languageCode: LanguageCode? = null): Result<Spaces>

    public suspend fun getBlocks(spaceId: SpaceIdentifier, languageCode: LanguageCode? = null): Result<Blocks>

    public suspend fun resolveTranslation(spaceId: SpaceIdentifier?, languageCode: LanguageCode): Result<Translation>

    public suspend fun getTranslations(spaceId: SpaceIdentifier?): Result<Translations>

    public suspend fun onSpaceCreated(block: suspend (ClientWebSocketEvent<Space>) -> Unit): Result<Job>

    public suspend fun onSpaceUpdated(
        id: SpaceId? = null,
        block: suspend (ClientWebSocketEvent<EntityUpdate<Space>>) -> Unit
    ): Result<Job>

    public suspend fun onSpaceRemoved(
        id: SpaceId? = null,
        block: suspend (ClientWebSocketEvent<Space>) -> Unit
    ): Result<Job>

    public suspend fun onBlockCreated(block: suspend (ClientWebSocketEvent<Block>) -> Unit): Result<Job>

    public suspend fun onBlockUpdated(
        id: BlockId? = null,
        block: suspend (ClientWebSocketEvent<EntityUpdate<Block>>) -> Unit
    ): Result<Job>

    public suspend fun onBlockRemoved(
        id: BlockId? = null,
        block: suspend (ClientWebSocketEvent<Block>) -> Unit
    ): Result<Job>

    public suspend fun isCompatible(): Result<Boolean>

    public fun isCompatible(daemonVersion: Version): Boolean
}

public fun PublicBeamClient(httpBaseUrl: BeamDaemonBaseUrl, wsBaseUrl: BeamDaemonBaseUrl? = null): PublicBeamClient =
    DefaultPublicBeamClient(httpBaseUrl, wsBaseUrl)

public class DefaultPublicBeamClient(
    override val httpBaseUrl: BeamDaemonBaseUrl,
    override val wsBaseUrl: BeamDaemonBaseUrl?
) : PublicBeamClient {

    override val resolver: SpaceResolver by lazy {
        DefaultSpaceResolver(this)
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }

        if (wsBaseUrl != null) {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }

            install(WebSocketEvents) {
                url = wsBaseUrl
            }
        }

        defaultRequest {
            url(httpBaseUrl)
        }

        expectSuccess = true
    }

    private val eventHandlingScope = CoroutineScope(Dispatchers.Main)

    override suspend fun getDaemonStatus(): Result<DaemonStatus> =
        runCatching {
            httpClient.get(Paths.GET_DAEMON_STATUS_ROUTE).body()
        }

    override suspend fun postSpace(space: SpaceModification): Result<SpaceWithToken> =
        runCatching {
            httpClient.post(Paths.POST_SPACE) {
                contentType(ContentType.Application.Json)
                setBody(space)
            }.body()
        }

    override suspend fun postSpace(build: SpaceModificationBuilder.() -> Unit): Result<SpaceWithToken> =
        postSpace(SpaceModificationBuilder().apply(build).build())

    override suspend fun postRootSpace(space: RootSpaceModification): Result<SpaceWithToken> =
        runCatching {
            httpClient.post(Paths.POST_ROOT_SPACE) {
                contentType(ContentType.Application.Json)
                setBody(space)
            }.body()
        }

    override suspend fun postRootSpace(build: RootSpaceModificationBuilder.() -> Unit): Result<SpaceWithToken> =
        postRootSpace(RootSpaceModificationBuilder().apply(build).build())

    public override suspend fun getSpace(id: SpaceIdentifier, languageCode: LanguageCode?): Result<Space> =
        runCatching {
            val path = Paths.GET_SPACE.replaceIdPlaceholder(id)

            httpClient.get(path) {
                setOptionalLanguageCode(languageCode)
            }.body()
        }

    public override suspend fun getSpaces(limitAndOffset: LimitAndOffset, languageCode: LanguageCode?): Result<Spaces> =
        runCatching {
            httpClient.get(Paths.GET_SPACES) {
                parameter(Paths.LIMIT_QUERY_PARAMETER, limitAndOffset.limit)
                parameter(Paths.OFFSET_QUERY_PARAMETER, limitAndOffset.offset)
                setOptionalLanguageCode(languageCode)
            }.body()
        }

    public override suspend fun getSpaces(limit: Int, offset: Int, languageCode: LanguageCode?): Result<Spaces> =
        getSpaces(LimitAndOffset(limit, offset))

    public override suspend fun getBlocks(spaceId: SpaceIdentifier, languageCode: LanguageCode?): Result<Blocks> =
        runCatching {
            httpClient.get(Paths.GET_BLOCKS) {
                parameter(Paths.SPACE_ID_QUERY_PARAMETER, spaceId)
                setOptionalLanguageCode(languageCode)
            }.body()
        }

    override suspend fun resolveTranslation(
        spaceId: SpaceIdentifier?,
        languageCode: LanguageCode
    ): Result<Translation> =
        runCatching {
            httpClient.get(Paths.GET_RESOLVED_TRANSLATION) {
                parameter(Paths.SPACE_ID_QUERY_PARAMETER, spaceId)
                parameter(Paths.LANGUAGE_CODE_QUERY_PARAMETER, languageCode)
            }.body()
        }

    override suspend fun getTranslations(spaceId: SpaceIdentifier?): Result<Translations> =
        runCatching {
            httpClient.get(Paths.GET_TRANSLATIONS) {
                parameter(Paths.SPACE_ID_QUERY_PARAMETER, spaceId)
            }.body()
        }

    override suspend fun onSpaceCreated(block: suspend (ClientWebSocketEvent<Space>) -> Unit): Result<Job> =
        handleWsEvents(EventReferences.spaceCreated, block)

    override suspend fun onSpaceUpdated(
        id: SpaceId?,
        block: suspend (ClientWebSocketEvent<EntityUpdate<Space>>) -> Unit
    ): Result<Job> {
        val reference = EventReferences.spaceUpdated(id)

        return handleWsEvents(reference, block)
    }

    override suspend fun onSpaceRemoved(
        id: SpaceId?,
        block: suspend (ClientWebSocketEvent<Space>) -> Unit
    ): Result<Job> {
        val reference = EventReferences.spaceRemoved(id)

        return handleWsEvents(reference, block)
    }

    override suspend fun onBlockCreated(block: suspend (ClientWebSocketEvent<Block>) -> Unit): Result<Job> =
        handleWsEvents(EventReferences.blockCreated, block)

    override suspend fun onBlockUpdated(
        id: BlockId?,
        block: suspend (ClientWebSocketEvent<EntityUpdate<Block>>) -> Unit
    ): Result<Job> {
        val reference = EventReferences.blockUpdated(id)

        return handleWsEvents(reference, block)
    }

    override suspend fun onBlockRemoved(
        id: BlockId?,
        block: suspend (ClientWebSocketEvent<Block>) -> Unit
    ): Result<Job> {
        val reference = EventReferences.blockRemoved(id)

        return handleWsEvents(reference, block)
    }

    override suspend fun isCompatible(): Result<Boolean> =
        runCatching {
            val status = getDaemonStatus().getOrThrow()

            isCompatible(status.version)
        }

    override fun isCompatible(daemonVersion: Version): Boolean =
        daemonVersion == VERSION

    private fun HttpRequestBuilder.setOptionalLanguageCode(languageCode: LanguageCode?) {
        languageCode?.let {
            parameter(Paths.LANGUAGE_CODE_QUERY_PARAMETER, it)
        }
    }

    private inline fun <reified T> handleWsEvents(
        reference: EventReference,
        crossinline handler: suspend (ClientWebSocketEvent<T>) -> Unit
    ) = runCatching {
        requireWsBaseUrl()

        eventHandlingScope.launch {
            httpClient.webSocketEvents(reference) {
                while (true) {
                    val event = try {
                        receiveWebSocketEvent<T>()
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        delay(5.seconds)

                        return@webSocketEvents
                    }

                    handler(event)
                }
            }
        }
    }

    private fun requireWsBaseUrl() =
        requireNotNull(wsBaseUrl) {
            "WebSocket base URL is required for this interaction."
        }
}