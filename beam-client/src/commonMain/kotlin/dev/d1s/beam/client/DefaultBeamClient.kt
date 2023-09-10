package dev.d1s.beam.client

import dev.d1s.beam.client.response.Spaces
import dev.d1s.beam.commons.*
import dev.d1s.beam.commons.event.EntityUpdate
import dev.d1s.beam.commons.event.EventReferences
import dev.d1s.exkt.common.pagination.LimitAndOffset
import dev.d1s.exkt.common.replaceIdPlaceholder
import dev.d1s.exkt.common.replacePlaceholders
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

public class DefaultBeamClient(
    override val httpBaseUrl: BeamDaemonBaseUrl,
    override val wsBaseUrl: BeamDaemonBaseUrl?,
    override val token: SpaceToken?
) : BeamClient {

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

            token?.let {
                bearerAuth(it)
            }
        }

        expectSuccess = true
    }

    private val eventHandlingScope = CoroutineScope(Dispatchers.Main)

    override suspend fun getDaemonStatus(): Result<DaemonStatus> =
        runCatching {
            httpClient.get(Paths.GET_DAEMON_STATUS_ROUTE).body()
        }

    public override suspend fun postSpace(
        space: SpaceModification,
        languageCode: LanguageCode?
    ): Result<SpaceWithToken> =
        runCatching {
            httpClient.post(Paths.POST_SPACE) {
                contentType(ContentType.Application.Json)
                setBody(space)
                setLanguageCode(languageCode)
            }.body()
        }

    public override suspend fun postSpace(
        languageCode: LanguageCode?,
        build: SpaceModificationBuilder.() -> Unit
    ): Result<SpaceWithToken> =
        postSpace(SpaceModificationBuilder().apply(build).build(), languageCode)

    public override suspend fun postRootSpace(
        space: RootSpaceModification,
        languageCode: LanguageCode?
    ): Result<SpaceWithToken> =
        runCatching {
            httpClient.post(Paths.POST_ROOT_SPACE) {
                contentType(ContentType.Application.Json)
                setBody(space)
                setLanguageCode(languageCode)
            }.body()
        }

    public override suspend fun postRootSpace(
        languageCode: LanguageCode?,
        build: RootSpaceModificationBuilder.() -> Unit
    ): Result<SpaceWithToken> =
        postRootSpace(RootSpaceModificationBuilder().apply(build).build(), languageCode)

    public override suspend fun getSpace(id: SpaceIdentifier, languageCode: LanguageCode?): Result<Space> =
        runCatching {
            val path = Paths.GET_SPACE.replaceIdPlaceholder(id)

            httpClient.get(path) {
                setLanguageCode(languageCode)
            }.body()
        }

    public override suspend fun getSpaces(limitAndOffset: LimitAndOffset, languageCode: LanguageCode?): Result<Spaces> =
        runCatching {
            httpClient.get(Paths.GET_SPACES) {
                parameter(Paths.LIMIT_QUERY_PARAMETER, limitAndOffset.limit)
                parameter(Paths.OFFSET_QUERY_PARAMETER, limitAndOffset.offset)
                setLanguageCode(languageCode)
            }.body()
        }

    public override suspend fun getSpaces(limit: Int, offset: Int, languageCode: LanguageCode?): Result<Spaces> =
        getSpaces(LimitAndOffset(limit, offset), languageCode)

    override suspend fun putSpace(
        id: SpaceIdentifier,
        space: SpaceModification,
        languageCode: LanguageCode?
    ): Result<Space> =
        runCatching {
            requireToken()

            val path = Paths.PUT_SPACE.replaceIdPlaceholder(id)

            httpClient.put(path) {
                contentType(ContentType.Application.Json)
                setBody(space)
                setLanguageCode(languageCode)
            }.body()
        }

    override suspend fun putSpace(
        id: SpaceIdentifier,
        languageCode: LanguageCode?,
        build: SpaceModificationBuilder.() -> Unit
    ): Result<Space> =
        putSpace(id, SpaceModificationBuilder().apply(build).build(), languageCode)

    override suspend fun putRootSpace(space: RootSpaceModification, languageCode: LanguageCode?): Result<Space> =
        runCatching {
            requireToken()

            httpClient.put(Paths.PUT_ROOT_SPACE) {
                contentType(ContentType.Application.Json)
                setBody(space)
                setLanguageCode(languageCode)
            }.body()
        }

    override suspend fun putRootSpace(
        languageCode: LanguageCode?,
        build: RootSpaceModificationBuilder.() -> Unit
    ): Result<Space> =
        putRootSpace(RootSpaceModificationBuilder().apply(build).build(), languageCode)

    override suspend fun deleteSpace(id: SpaceIdentifier): Result<Unit> =
        runCatching {
            requireToken()

            val path = Paths.DELETE_SPACE.replaceIdPlaceholder(id)

            httpClient.delete(path)
        }

    override suspend fun postBlock(block: BlockModification, languageCode: LanguageCode?): Result<Block> =
        runCatching {
            requireToken()

            httpClient.post(Paths.POST_BLOCK) {
                contentType(ContentType.Application.Json)
                setBody(block)
                setLanguageCode(languageCode)
            }.body()
        }

    override suspend fun postBlock(
        languageCode: LanguageCode?,
        build: BlockModificationBuilder.() -> Unit
    ): Result<Block> =
        postBlock(BlockModificationBuilder().apply(build).build(), languageCode)

    public override suspend fun getBlocks(spaceId: SpaceIdentifier, languageCode: LanguageCode?): Result<Blocks> =
        runCatching {
            httpClient.get(Paths.GET_BLOCKS) {
                parameter(Paths.SPACE_ID_QUERY_PARAMETER, spaceId)
                setLanguageCode(languageCode)
            }.body()
        }

    override suspend fun putBlock(id: BlockId, block: BlockModification, languageCode: LanguageCode?): Result<Block> =
        runCatching {
            requireToken()

            val path = Paths.PUT_BLOCK.replaceIdPlaceholder(id)

            httpClient.put(path) {
                contentType(ContentType.Application.Json)
                setBody(block)
                setLanguageCode(languageCode)
            }.body()
        }

    override suspend fun putBlock(
        id: BlockId,
        languageCode: LanguageCode?,
        build: BlockModificationBuilder.() -> Unit
    ): Result<Block> =
        putBlock(id, BlockModificationBuilder().apply(build).build(), languageCode)

    override suspend fun deleteBlock(id: BlockId): Result<Unit> =
        runCatching {
            requireToken()

            val path = Paths.DELETE_BLOCK.replaceIdPlaceholder(id)

            httpClient.delete(path)
        }

    override suspend fun postTranslation(
        spaceId: SpaceIdentifier?,
        translation: TranslationModification
    ): Result<Translation> =
        runCatching {
            requireToken()

            httpClient.post(Paths.POST_TRANSLATION) {
                contentType(ContentType.Application.Json)
                setBody(translation)
                setSpaceId(spaceId)
            }.body()
        }

    override suspend fun postTranslation(
        spaceId: SpaceIdentifier?,
        build: TranslationModificationBuilder.() -> Unit
    ): Result<Translation> =
        postTranslation(spaceId, TranslationModificationBuilder().apply(build).build())

    override suspend fun getTranslation(spaceId: SpaceIdentifier?, languageCode: LanguageCode): Result<Translation> =
        runCatching {
            val path = Paths.GET_TRANSLATION.replaceLanguageCodePlaceholder(languageCode)

            httpClient.get(path) {
                setSpaceId(spaceId)
            }.body()
        }

    public override suspend fun getResolvedTranslation(
        spaceId: SpaceIdentifier?,
        languageCode: LanguageCode
    ): Result<Translation> =
        runCatching {
            httpClient.get(Paths.GET_RESOLVED_TRANSLATION) {
                setSpaceId(spaceId)
                setLanguageCode(languageCode)
            }.body()
        }

    override suspend fun getTranslations(spaceId: SpaceIdentifier?): Result<Translations> =
        runCatching {
            httpClient.get(Paths.GET_TRANSLATIONS) {
                setSpaceId(spaceId)
            }.body()
        }

    override suspend fun putTranslation(
        spaceId: SpaceIdentifier?,
        languageCode: LanguageCode,
        translation: TranslationModification
    ): Result<Translation> =
        runCatching {
            requireToken()

            val path = Paths.PUT_TRANSLATION.replaceLanguageCodePlaceholder(languageCode)

            httpClient.put(path) {
                contentType(ContentType.Application.Json)
                setBody(translation)
                setSpaceId(spaceId)
            }.body()
        }

    override suspend fun putTranslation(
        spaceId: SpaceIdentifier?,
        languageCode: LanguageCode,
        build: TranslationModificationBuilder.() -> Unit
    ): Result<Translation> =
        putTranslation(spaceId, languageCode, TranslationModificationBuilder().apply(build).build())

    override suspend fun deleteTranslation(spaceId: SpaceIdentifier?, languageCode: LanguageCode): Result<Unit> =
        runCatching {
            requireToken()

            val path = Paths.DELETE_TRANSLATION.replaceLanguageCodePlaceholder(languageCode)

            httpClient.delete(path) {
                setSpaceId(spaceId)
            }
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

    private fun HttpRequestBuilder.setLanguageCode(languageCode: LanguageCode?) {
        parameter(Paths.LANGUAGE_CODE_QUERY_PARAMETER, languageCode)
    }

    private fun HttpRequestBuilder.setSpaceId(spaceId: SpaceIdentifier?) {
        parameter(Paths.SPACE_ID_QUERY_PARAMETER, spaceId)
    }

    private fun String.replaceLanguageCodePlaceholder(languageCode: LanguageCode) =
        replacePlaceholders(Paths.LANGUAGE_CODE_PARAMETER to languageCode)

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

                        continue
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

    private fun requireToken() =
        requireNotNull(token) {
            "Space token is required for this interaction."
        }
}