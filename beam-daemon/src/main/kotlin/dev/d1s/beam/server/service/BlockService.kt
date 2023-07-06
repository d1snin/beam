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
import dev.d1s.beam.commons.event.EntityUpdate
import dev.d1s.beam.commons.event.EventReferences
import dev.d1s.beam.server.configuration.DtoConverters
import dev.d1s.beam.server.database.BlockRepository
import dev.d1s.beam.server.entity.BlockEntity
import dev.d1s.beam.server.entity.asString
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
        uniqueIdentifier: String,
        requireDto: Boolean = false
    ): ResultingEntityWithOptionalDto<BlockEntity, Block>

    suspend fun getBlocks(requireDto: Boolean = false): ResultingEntityWithOptionalDtoList<BlockEntity, Block>

    suspend fun updateBlock(
        uniqueIdentifier: String,
        modification: BlockEntity
    ): ResultingEntityWithOptionalDto<BlockEntity, Block>

    suspend fun removeBlock(uniqueIdentifier: String): Result<Unit>
}

internal class DefaultBlockService : BlockService, KoinComponent {

    private val blockRepository by inject<BlockRepository>()

    private val blockDtoConverter by inject<DtoConverter<BlockEntity, Block>>(qualifier = DtoConverters.BlockDtoConverterQualifier)

    private val eventChannel by inject<WebSocketEventChannel>()

    private val logger = logging()

    override suspend fun createBlock(block: BlockEntity): ResultingEntityWithOptionalDto<BlockEntity, Block> =
        runCatching {
            logger.d {
                "Creating block ${block.asString}..."
            }

            val addedBlock = blockRepository.addBlock(block).getOrThrow()
            val addedBlockDto = blockDtoConverter.convertToDto(addedBlock)

            sendBlockCreatedEvent(addedBlockDto)

            addedBlock to addedBlockDto
        }

    override suspend fun getBlock(
        uniqueIdentifier: String,
        requireDto: Boolean
    ): ResultingEntityWithOptionalDto<BlockEntity, Block> =
        runCatching {
            logger.d {
                "Obtaining block with unique identifier $uniqueIdentifier..."
            }

            val uuid = runCatching {
                UUID.fromString(uniqueIdentifier)
            }.getOrNull()

            val block = uuid?.let {
                blockRepository.findBlockById(it).getOrNull()
            } ?: blockRepository.findBlockBySlug(uniqueIdentifier).getOrElse {
                throw NotFoundException(it.message)
            }

            block to blockDtoConverter.convertToDtoIf(block) {
                requireDto
            }
        }

    override suspend fun getBlocks(requireDto: Boolean): ResultingEntityWithOptionalDtoList<BlockEntity, Block> =
        runCatching {
            logger.d {
                "Obtaining blocks..."
            }

            val blocks = blockRepository.findAllBlocks().getOrThrow()

            blocks to blockDtoConverter.convertToDtoListIf(blocks) {
                requireDto
            }
        }

    override suspend fun updateBlock(
        uniqueIdentifier: String,
        modification: BlockEntity
    ): ResultingEntityWithOptionalDto<BlockEntity, Block> =
        runCatching {
            logger.d {
                "Updating block with unique identifier $uniqueIdentifier with data ${modification.asString}..."
            }

            val (originalBlock, originalBlockDto) = getBlock(uniqueIdentifier, requireDto = true).getOrThrow()
            requireNotNull(originalBlockDto)

            originalBlock.apply {
                this.slug = modification.slug
                this.index = modification.index
                this.size = modification.size
                this.entities = modification.entities
            }

            val updatedBlock = blockRepository.updateBlock(originalBlock).getOrThrow()
            val updatedBlockDto = blockDtoConverter.convertToDto(updatedBlock)

            sendBlockUpdatedEvent(originalBlockDto, updatedBlockDto)

            updatedBlock to updatedBlockDto
        }

    override suspend fun removeBlock(uniqueIdentifier: String): Result<Unit> =
        runCatching {
            logger.d {
                "Removing block with unique identifier $uniqueIdentifier..."
            }

            val (block, blockDto) = getBlock(uniqueIdentifier, requireDto = true).getOrThrow()
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