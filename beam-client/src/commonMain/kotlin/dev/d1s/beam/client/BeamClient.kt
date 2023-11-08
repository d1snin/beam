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
import dev.d1s.exkt.common.pagination.LimitAndOffset
import dev.d1s.ktor.events.client.ClientWebSocketEvent
import kotlinx.coroutines.Job

public typealias BeamDaemonBaseUrl = String

public interface BeamClient {

    public val httpBaseUrl: BeamDaemonBaseUrl

    public val wsBaseUrl: BeamDaemonBaseUrl?

    public val token: SpaceToken?

    public val resolver: SpaceResolver

    public suspend fun getDaemonStatus(): Result<DaemonStatus>

    public suspend fun postSpace(space: SpaceModification, languageCode: LanguageCode? = null): Result<SpaceWithToken>

    public suspend fun postSpace(
        languageCode: LanguageCode? = null,
        configure: suspend SpaceModificationBuilder.() -> Unit
    ): Result<SpaceWithToken>

    public suspend fun postRootSpace(
        space: RootSpaceModification,
        languageCode: LanguageCode? = null
    ): Result<SpaceWithToken>

    public suspend fun postRootSpace(
        languageCode: LanguageCode? = null,
        configure: suspend RootSpaceModificationBuilder.() -> Unit
    ): Result<SpaceWithToken>

    public suspend fun getSpace(id: SpaceIdentifier, languageCode: LanguageCode? = null): Result<Space>

    public suspend fun getSpaces(limitAndOffset: LimitAndOffset, languageCode: LanguageCode? = null): Result<Spaces>

    public suspend fun getSpaces(limit: Int, offset: Int, languageCode: LanguageCode? = null): Result<Spaces>

    public suspend fun putSpace(
        id: SpaceIdentifier,
        space: SpaceModification,
        languageCode: LanguageCode? = null
    ): Result<Space>

    public suspend fun putSpace(
        id: SpaceIdentifier,
        languageCode: LanguageCode? = null,
        configure: suspend SpaceModificationBuilder.() -> Unit
    ): Result<Space>

    public suspend fun putRootSpace(space: RootSpaceModification, languageCode: LanguageCode? = null): Result<Space>

    public suspend fun putRootSpace(
        languageCode: LanguageCode? = null,
        configure: suspend RootSpaceModificationBuilder.() -> Unit
    ): Result<Space>

    public suspend fun deleteSpace(id: SpaceIdentifier): Result<Unit>

    public suspend fun postBlock(block: BlockModification, languageCode: LanguageCode? = null): Result<Block>

    public suspend fun postBlock(
        languageCode: LanguageCode? = null,
        configure: suspend BlockModificationBuilder.() -> Unit
    ): Result<Block>

    public suspend fun getBlocks(spaceId: SpaceIdentifier, languageCode: LanguageCode? = null): Result<Blocks>

    public suspend fun putBlock(
        id: BlockId,
        block: BlockModification,
        languageCode: LanguageCode? = null
    ): Result<Block>

    public suspend fun putBlock(
        id: BlockId,
        languageCode: LanguageCode? = null,
        configure: suspend BlockModificationBuilder.() -> Unit
    ): Result<Block>

    public suspend fun deleteBlock(id: BlockId): Result<Unit>

    public suspend fun getRow(index: RowIndex, spaceId: SpaceIdentifier): Result<Row>

    public suspend fun getRows(spaceId: SpaceIdentifier): Result<Rows>

    public suspend fun putRow(
        index: RowIndex,
        spaceId: SpaceIdentifier,
        row: RowModification
    ): Result<Row>

    public suspend fun putRow(
        index: RowIndex,
        spaceId: SpaceIdentifier,
        configure: suspend RowModificationBuilder.() -> Unit
    ): Result<Row>

    public suspend fun postTranslation(
        spaceId: SpaceIdentifier? = null,
        translation: TranslationModification
    ): Result<Translation>

    public suspend fun postTranslation(
        spaceId: SpaceIdentifier? = null,
        configure: suspend TranslationModificationBuilder.() -> Unit
    ): Result<Translation>

    public suspend fun getTranslation(spaceId: SpaceIdentifier? = null, languageCode: LanguageCode): Result<Translation>

    public suspend fun getResolvedTranslation(
        spaceId: SpaceIdentifier? = null,
        languageCode: LanguageCode
    ): Result<Translation>

    public suspend fun getTranslations(spaceId: SpaceIdentifier? = null): Result<Translations>

    public suspend fun putTranslation(
        spaceId: SpaceIdentifier? = null,
        languageCode: LanguageCode,
        translation: TranslationModification
    ): Result<Translation>

    public suspend fun putTranslation(
        spaceId: SpaceIdentifier? = null,
        languageCode: LanguageCode,
        configure: suspend TranslationModificationBuilder.() -> Unit
    ): Result<Translation>

    public suspend fun deleteTranslation(spaceId: SpaceIdentifier? = null, languageCode: LanguageCode): Result<Unit>

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

    public suspend fun onRowCreated(block: suspend (ClientWebSocketEvent<Row>) -> Unit): Result<Job>

    public suspend fun onRowUpdated(
        qualifier: RowQualifier? = null,
        block: suspend (ClientWebSocketEvent<EntityUpdate<Row>>) -> Unit
    ): Result<Job>

    public suspend fun isCompatible(): Result<Boolean>

    public fun isCompatible(daemonVersion: Version): Boolean

    public fun addBlockMiddleware(middleware: BlockMiddleware)
}

public fun BeamClient(
    httpBaseUrl: BeamDaemonBaseUrl,
    wsBaseUrl: BeamDaemonBaseUrl? = null,
    token: SpaceToken? = null
): BeamClient =
    DefaultBeamClient(httpBaseUrl, wsBaseUrl, token)