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

package dev.d1s.beam.daemon.service

import com.auth0.jwt.JWT
import dev.d1s.beam.commons.BlockId
import dev.d1s.beam.commons.SpaceId
import dev.d1s.beam.commons.SpaceIdentifier
import dev.d1s.beam.commons.SpaceToken
import dev.d1s.beam.daemon.configuration.Security
import dev.d1s.beam.daemon.configuration.jwtIssuer
import dev.d1s.beam.daemon.configuration.jwtSubject
import dev.d1s.beam.daemon.entity.SpaceEntity
import dev.d1s.beam.daemon.entity.isRoot
import dev.d1s.beam.daemon.util.requiredIdParameter
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface AuthService {

    fun createToken(space: SpaceEntity): SpaceToken

    suspend fun isSpaceModificationAllowed(call: ApplicationCall): Boolean

    suspend fun isSpaceModificationAllowed(subject: SpaceId, space: SpaceIdentifier): Result<Boolean>

    suspend fun isBlockModificationAllowed(call: ApplicationCall): Boolean

    suspend fun isBlockModificationAllowed(subject: SpaceId, block: BlockId): Result<Boolean>
}

class DefaultAuthService : AuthService, KoinComponent {

    private val config by inject<ApplicationConfig>()

    private val jwtIssuer by lazy {
        config.jwtIssuer
    }

    private val spaceService by inject<SpaceService>()

    private val blockService by inject<BlockService>()

    override fun createToken(space: SpaceEntity): SpaceToken {
        val spaceId = space.id.toString()

        return JWT.create()
            .withIssuer(jwtIssuer)
            .withSubject(spaceId)
            .sign(Security.jwtAlgorithm)
    }

    override suspend fun isSpaceModificationAllowed(call: ApplicationCall): Boolean =
        isSpaceModificationAllowed(call.jwtSubject, call.requiredIdParameter).getOrThrow()

    override suspend fun isSpaceModificationAllowed(subject: SpaceId, space: SpaceIdentifier): Result<Boolean> =
        runCatching {
            val (subjectSpace, _) = spaceService.getSpace(subject).getOrThrow()
            val (spaceToModify, _) = spaceService.getSpace(space).getOrThrow()

            subjectSpace.isRoot || subjectSpace.id == spaceToModify.id
        }

    override suspend fun isBlockModificationAllowed(call: ApplicationCall): Boolean =
        isBlockModificationAllowed(call.jwtSubject, call.requiredIdParameter).getOrThrow()

    override suspend fun isBlockModificationAllowed(subject: SpaceId, block: BlockId): Result<Boolean> =
        runCatching {
            val (subjectSpace, _) = spaceService.getSpace(subject).getOrThrow()
            val (blockToModify, _) = blockService.getBlock(block).getOrThrow()

            subjectSpace.isRoot || blockToModify.space.id == subjectSpace.id
        }
}