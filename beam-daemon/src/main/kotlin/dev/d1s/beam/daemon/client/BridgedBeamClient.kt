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

package dev.d1s.beam.daemon.client

import dev.d1s.beam.client.*
import dev.d1s.beam.client.response.Blocks
import dev.d1s.beam.client.response.Spaces
import dev.d1s.beam.commons.*
import dev.d1s.beam.commons.event.EntityUpdate
import dev.d1s.beam.daemon.service.SpaceService
import dev.d1s.beam.daemon.service.TranslationService
import dev.d1s.exkt.common.pagination.LimitAndOffset
import dev.d1s.exkt.dto.dto
import dev.d1s.ktor.events.client.ClientWebSocketEvent
import kotlinx.coroutines.Job
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BridgedBeamClient : BeamClient, KoinComponent {

    override val httpBaseUrl get() = notOnNetworkError()
    override val wsBaseUrl get() = notOnNetworkError()
    override val token get() = notOnNetworkError()

    override val resolver: SpaceResolver by lazy {
        DefaultSpaceResolver(this)
    }

    override val httpClient get() = notOnNetworkError()

    private val spaceService by inject<SpaceService>()

    private val translationService by inject<TranslationService>()

    override suspend fun getDaemonStatus(): Result<DaemonStatus> =
        runCatching {
            DaemonStatus(VERSION, DaemonState.UP)
        }

    override suspend fun postSpace(space: SpaceModification, languageCode: LanguageCode?): Result<Space> =
        readOnlyError()

    override suspend fun postSpace(
        languageCode: LanguageCode?,
        configure: suspend SpaceModificationBuilder.() -> Unit
    ) = readOnlyError()

    override suspend fun postRootSpace(
        space: RootSpaceModification,
        languageCode: LanguageCode?
    ) = readOnlyError()

    override suspend fun postRootSpace(
        languageCode: LanguageCode?,
        configure: suspend RootSpaceModificationBuilder.() -> Unit
    ) = readOnlyError()

    override suspend fun getSpace(id: SpaceIdentifier, languageCode: LanguageCode?): Result<Space> =
        runCatching {
            val space = spaceService.getSpace(id, languageCode, requireDto = true).getOrThrow().dto
            requireNotNull(space)
        }


    override suspend fun getSpaces(limitAndOffset: LimitAndOffset, languageCode: LanguageCode?): Result<Spaces> =
        getSpaces(limitAndOffset.limit, limitAndOffset.offset, languageCode)

    override suspend fun getSpaces(limit: Int, offset: Int, languageCode: LanguageCode?): Result<Spaces> =
        runCatching {
            val spacesDto =
                spaceService.getSpaces(limit, offset, languageCode, requireDto = true)
                    .getOrThrow()
                    .dto

            requireNotNull(spacesDto)

            with(spacesDto) {
                Spaces(limit, offset, totalCount, elements)
            }
        }

    override suspend fun putSpace(
        id: SpaceIdentifier,
        space: SpaceModification,
        languageCode: LanguageCode?
    ): Result<Space> = readOnlyError()

    override suspend fun putSpace(
        id: SpaceIdentifier,
        languageCode: LanguageCode?,
        configure: suspend SpaceModificationBuilder.() -> Unit
    ): Result<Space> = readOnlyError()

    override suspend fun putRootSpace(space: RootSpaceModification, languageCode: LanguageCode?): Result<Space> =
        readOnlyError()

    override suspend fun putRootSpace(
        languageCode: LanguageCode?,
        configure: suspend RootSpaceModificationBuilder.() -> Unit
    ): Result<Space> = readOnlyError()

    override suspend fun deleteSpace(id: SpaceIdentifier): Result<Unit> =
        readOnlyError()

    override suspend fun postBlock(block: BlockModification, languageCode: LanguageCode?): Result<Block> =
        readOnlyError()

    override suspend fun postBlock(
        languageCode: LanguageCode?,
        configure: suspend BlockModificationBuilder.() -> Unit
    ): Result<Block> =
        readOnlyError()

    override suspend fun getBlocks(
        spaceId: SpaceIdentifier,
        limitAndOffset: LimitAndOffset,
        languageCode: LanguageCode?
    ): Result<Blocks> =
        getBlocks(spaceId, limitAndOffset.limit, limitAndOffset.offset, languageCode)

    override suspend fun getBlocks(
        spaceId: SpaceIdentifier,
        limit: Int,
        offset: Int,
        languageCode: LanguageCode?
    ): Result<Blocks> =
        notYetImplementedError()

    override suspend fun iterateBlocks(
        spaceId: SpaceIdentifier,
        languageCode: LanguageCode?,
        onEach: suspend (Block) -> Unit
        ): Result<Unit> =
        notYetImplementedError()

    override suspend fun putBlock(id: BlockId, block: BlockModification, languageCode: LanguageCode?): Result<Block> =
        readOnlyError()

    override suspend fun putBlock(
        id: BlockId,
        languageCode: LanguageCode?,
        configure: suspend BlockModificationBuilder.() -> Unit
    ): Result<Block> =
        readOnlyError()

    override suspend fun deleteBlock(id: BlockId): Result<Unit> =
        readOnlyError()

    override suspend fun deleteBlocks(spaceId: SpaceIdentifier): Result<Unit> =
        readOnlyError()

    override suspend fun getRow(index: RowIndex, spaceId: SpaceIdentifier): Result<Row> =
        notYetImplementedError()

    override suspend fun getRows(spaceId: SpaceIdentifier): Result<Rows> =
        notYetImplementedError()

    override suspend fun putRow(index: RowIndex, spaceId: SpaceIdentifier, row: RowModification): Result<Row> =
        readOnlyError()

    override suspend fun putRow(
        index: RowIndex,
        spaceId: SpaceIdentifier,
        configure: suspend RowModificationBuilder.() -> Unit
    ): Result<Row> =
        readOnlyError()

    override suspend fun postTranslation(
        languageCode: LanguageCode,
        translation: TranslationModification
    ): Result<Translation> =
        readOnlyError()

    override suspend fun postTranslation(
        languageCode: LanguageCode,
        configure: suspend TranslationModificationBuilder.() -> Unit
    ): Result<Translation> =
        readOnlyError()

    override suspend fun getTranslation(languageCode: LanguageCode): Result<Translation> =
        notYetImplementedError()

    override suspend fun getResolvedTranslation(languageCode: LanguageCode): Result<Translation> =
        runCatching {
            val translation = translationService.resolveTranslation(languageCode, requireDto = true)
                .getOrThrow()
                .dto

            requireNotNull(translation)
        }

    override suspend fun getTranslations(): Result<Translations> =
        notYetImplementedError()

    override suspend fun putTranslation(
        languageCode: LanguageCode,
        translation: TranslationModification
    ): Result<Translation> =
        notYetImplementedError()

    override suspend fun putTranslation(
        languageCode: LanguageCode,
        configure: suspend TranslationModificationBuilder.() -> Unit
    ): Result<Translation> =
        notYetImplementedError()

    override suspend fun deleteTranslation(languageCode: LanguageCode): Result<Unit> =
        readOnlyError()

    override suspend fun onSpaceCreated(block: suspend (ClientWebSocketEvent<Space>) -> Unit): Result<Job> =
        notYetImplementedError()

    override suspend fun onSpaceUpdated(
        id: SpaceId?,
        block: suspend (ClientWebSocketEvent<EntityUpdate<Space>>) -> Unit
    ): Result<Job> =
        notYetImplementedError()

    override suspend fun onSpaceRemoved(
        id: SpaceId?,
        block: suspend (ClientWebSocketEvent<Space>) -> Unit
    ): Result<Job> =
        notYetImplementedError()

    override suspend fun onBlockCreated(block: suspend (ClientWebSocketEvent<Block>) -> Unit): Result<Job> =
        notYetImplementedError()

    override suspend fun onBlockUpdated(
        id: BlockId?,
        block: suspend (ClientWebSocketEvent<EntityUpdate<Block>>) -> Unit
    ): Result<Job> =
        notYetImplementedError()

    override suspend fun onBlockRemoved(
        id: BlockId?,
        block: suspend (ClientWebSocketEvent<Block>) -> Unit
    ): Result<Job> =
        notYetImplementedError()

    override suspend fun onRowCreated(block: suspend (ClientWebSocketEvent<Row>) -> Unit): Result<Job> =
        notYetImplementedError()

    override suspend fun onRowUpdated(
        qualifier: RowQualifier?,
        block: suspend (ClientWebSocketEvent<EntityUpdate<Row>>) -> Unit
    ): Result<Job> =
        notYetImplementedError()

    override suspend fun isCompatible(): Result<Boolean> =
        notYetImplementedError()

    override fun isCompatible(daemonVersion: Version): Boolean =
        notYetImplementedError()

    override fun addBlockMiddleware(middleware: BlockMiddleware) =
        readOnlyError()

    private fun notOnNetworkError(): Nothing = error("This client isn't on network")
    private fun readOnlyError(): Nothing = throw NotImplementedError("This client is read-only")
    private fun notYetImplementedError(): Nothing = throw NotImplementedError("This operation is not yet implemented")
}