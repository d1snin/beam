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

package dev.d1s.beam.server.service

import dev.d1s.beam.commons.Block
import dev.d1s.beam.commons.BlockId
import dev.d1s.beam.commons.SpaceIdentifier
import dev.d1s.beam.commons.event.EntityUpdate
import dev.d1s.beam.commons.event.EventReferences
import dev.d1s.beam.server.configuration.DtoConverters
import dev.d1s.beam.server.database.BlockRepository
import dev.d1s.beam.server.entity.BlockEntity
import dev.d1s.beam.server.entity.SpaceEntity
import dev.d1s.beam.server.entity.asString
import dev.d1s.beam.server.exception.UnprocessableEntityException
import dev.d1s.exkt.dto.*
import dev.d1s.ktor.events.commons.event
import dev.d1s.ktor.events.server.WebSocketEventChannel
import io.ktor.server.plugins.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import java.util.*

internal interface BlockService {

    suspend fun createBlock(block: BlockEntity): ResultingEntityWithOptionalDto<BlockEntity, Block>

    suspend fun getBlock(
        id: BlockId,
        requireDto: Boolean = false
    ): ResultingEntityWithOptionalDto<BlockEntity, Block>

    suspend fun getBlocks(
        spaceIdentifier: SpaceIdentifier,
        requireDto: Boolean = false
    ): ResultingEntityWithOptionalDtoList<BlockEntity, Block>

    suspend fun updateBlock(
        id: BlockId,
        modification: BlockEntity
    ): ResultingEntityWithOptionalDto<BlockEntity, Block>

    suspend fun removeBlock(id: BlockId): Result<Unit>
}

internal class DefaultBlockService : BlockService, KoinComponent {

    private val blockRepository by inject<BlockRepository>()

    private val blockDtoConverter by inject<DtoConverter<BlockEntity, Block>>(qualifier = DtoConverters.BlockDtoConverterQualifier)

    private val eventChannel by inject<WebSocketEventChannel>()

    private val spaceService by inject<SpaceService>()

    private val logger = logging()

    override suspend fun createBlock(block: BlockEntity): ResultingEntityWithOptionalDto<BlockEntity, Block> =
        runCatching {
            logger.d {
                "Creating block ${block.asString}..."
            }

            val space = block.space
            val count = blockRepository.countBlocksInSpace(space).getOrThrow()

            if (count >= SpaceEntity.SPACE_CAPACITY) {
                logger.w {
                    "Space ${space.id} reached it's capacity. Unable to process block creation"
                }

                throw UnprocessableEntityException("Space capacity reached")
            }

            val addedBlock = blockRepository.addBlock(block).getOrThrow()
            val addedBlockDto = blockDtoConverter.convertToDto(addedBlock)

            sendBlockCreatedEvent(addedBlockDto)

            addedBlock to addedBlockDto
        }

    override suspend fun getBlock(
        id: BlockId,
        requireDto: Boolean
    ): ResultingEntityWithOptionalDto<BlockEntity, Block> =
        runCatching {
            logger.d {
                "Obtaining block with ID $id..."
            }

            val uuid = runCatching {
                UUID.fromString(id)
            }.getOrNull()

            val block = uuid?.let { id ->
                blockRepository.findBlockById(id).getOrNull()
            } ?: throw NotFoundException("Block not found")

            block to blockDtoConverter.convertToDtoIf(block) {
                requireDto
            }
        }

    override suspend fun getBlocks(
        spaceIdentifier: SpaceIdentifier,
        requireDto: Boolean
    ): ResultingEntityWithOptionalDtoList<BlockEntity, Block> =
        runCatching {
            logger.d {
                "Obtaining blocks for space with unique identifier $spaceIdentifier..."
            }

            val (space, _) = spaceService.getSpace(spaceIdentifier).getOrThrow()

            val blocks = blockRepository.findBlocksInSpace(space).getOrThrow()

            blocks to blockDtoConverter.convertToDtoListIf(blocks) {
                requireDto
            }
        }

    override suspend fun updateBlock(
        id: BlockId,
        modification: BlockEntity
    ): ResultingEntityWithOptionalDto<BlockEntity, Block> =
        runCatching {
            logger.d {
                "Updating block with ID $id with data ${modification.asString}..."
            }

            val (originalBlock, originalBlockDto) = getBlock(id, requireDto = true).getOrThrow()
            requireNotNull(originalBlockDto)

            originalBlock.apply {
                this.index = modification.index
                this.size = modification.size
                this.entities = modification.entities
            }

            val updatedBlock = blockRepository.updateBlock(originalBlock).getOrThrow()
            val updatedBlockDto = blockDtoConverter.convertToDto(updatedBlock)

            sendBlockUpdatedEvent(originalBlockDto, updatedBlockDto)

            updatedBlock to updatedBlockDto
        }

    override suspend fun removeBlock(id: BlockId): Result<Unit> =
        runCatching {
            logger.d {
                "Removing block with ID $id..."
            }

            val (block, blockDto) = getBlock(id, requireDto = true).getOrThrow()
            requireNotNull(blockDto)

            blockRepository.removeBlock(block).getOrThrow()

            sendBlockRemovedEvent(blockDto)
        }

    private suspend fun sendBlockCreatedEvent(blockDto: Block) {
        val event = event(EventReferences.blockCreated, blockDto)
        eventChannel.send(event)
    }

    private suspend fun sendBlockUpdatedEvent(oldBlockDto: Block, newBlockDto: Block) {
        val eventRef = EventReferences.blockUpdated(newBlockDto.id)
        val update = EntityUpdate(oldBlockDto, newBlockDto)
        val event = event(eventRef, update)
        eventChannel.send(event)
    }

    private suspend fun sendBlockRemovedEvent(blockDto: Block) {
        val eventRef = EventReferences.blockRemoved(blockDto.id)
        val event = event(eventRef, blockDto)
        eventChannel.send(event)
    }
}