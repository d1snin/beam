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

import dev.d1s.beam.commons.Block
import dev.d1s.beam.commons.BlockId
import dev.d1s.beam.commons.LanguageCode
import dev.d1s.beam.commons.SpaceIdentifier
import dev.d1s.beam.commons.event.EntityUpdate
import dev.d1s.beam.commons.event.EventReferences
import dev.d1s.beam.daemon.configuration.DtoConverters
import dev.d1s.beam.daemon.database.BlockRepository
import dev.d1s.beam.daemon.entity.BlockEntity
import dev.d1s.beam.daemon.entity.SpaceEntity
import dev.d1s.beam.daemon.entity.asString
import dev.d1s.beam.daemon.exception.UnprocessableEntityException
import dev.d1s.exkt.dto.*
import dev.d1s.ktor.events.server.WebSocketEventChannel
import dev.d1s.ktor.events.server.event
import io.ktor.server.plugins.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import java.util.*

interface BlockService {

    suspend fun createBlock(
        block: BlockEntity,
        languageCode: LanguageCode? = null
    ): ResultingEntityWithOptionalDto<BlockEntity, Block>

    suspend fun getBlock(
        id: BlockId,
        languageCode: LanguageCode? = null,
        requireDto: Boolean = false
    ): ResultingEntityWithOptionalDto<BlockEntity, Block>

    suspend fun getBlocks(
        spaceIdentifier: SpaceIdentifier,
        languageCode: LanguageCode? = null,
        requireDto: Boolean = false
    ): ResultingEntityWithOptionalDtoList<BlockEntity, Block>

    suspend fun updateBlock(
        id: BlockId,
        modification: BlockEntity,
        languageCode: LanguageCode? = null
    ): ResultingEntityWithOptionalDto<BlockEntity, Block>

    suspend fun removeBlock(id: BlockId): Result<Unit>
}

class DefaultBlockService : BlockService, KoinComponent {

    private val blockRepository by inject<BlockRepository>()

    private val blockDtoConverter by inject<DtoConverter<BlockEntity, Block>>(DtoConverters.BlockDtoConverterQualifier)

    private val eventChannel by inject<WebSocketEventChannel>()

    private val spaceService by inject<SpaceService>()

    private val translationService by inject<TranslationService>()

    private val logger = logging()

    override suspend fun createBlock(
        block: BlockEntity,
        languageCode: LanguageCode?
    ): ResultingEntityWithOptionalDto<BlockEntity, Block> =
        runCatching {
            logger.d {
                "Creating block ${block.asString}..."
            }

            checkBlockLimit(block)

            translationService.verifyLocationsExist(block).getOrThrow()

            processBlockIndex(block)

            val addedBlock = blockRepository.addBlock(block).getOrThrow()
            val translatedBlock = translateOptionally(addedBlock, languageCode)
            val translatedBlockDto = blockDtoConverter.convertToDto(translatedBlock)

            sendBlockCreatedEvent(translatedBlockDto)

            translatedBlock to translatedBlockDto
        }

    override suspend fun getBlock(
        id: BlockId,
        languageCode: LanguageCode?,
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

            val translatedBlock = translateOptionally(block, languageCode)

            translatedBlock to blockDtoConverter.convertToDtoIf(translatedBlock) {
                requireDto
            }
        }

    override suspend fun getBlocks(
        spaceIdentifier: SpaceIdentifier,
        languageCode: LanguageCode?,
        requireDto: Boolean
    ): ResultingEntityWithOptionalDtoList<BlockEntity, Block> =
        runCatching {
            logger.d {
                "Obtaining blocks for space with unique identifier $spaceIdentifier..."
            }

            val (space, _) = spaceService.getSpace(spaceIdentifier).getOrThrow()

            val blocks = blockRepository.findBlocksInSpace(space).getOrThrow()

            val translatedBlocks = translateOptionally(blocks, languageCode)

            translatedBlocks to blockDtoConverter.convertToDtoListIf(blocks) {
                requireDto
            }
        }

    override suspend fun updateBlock(
        id: BlockId,
        modification: BlockEntity,
        languageCode: LanguageCode?
    ): ResultingEntityWithOptionalDto<BlockEntity, Block> =
        runCatching {
            logger.d {
                "Updating block with ID $id with data ${modification.asString}..."
            }

            val (originalBlock, originalBlockDto) = getBlock(id, requireDto = true).getOrThrow()
            requireNotNull(originalBlockDto)

            translationService.verifyLocationsExist(modification).getOrThrow()

            originalBlock.apply {
                this.index = modification.index
                this.size = modification.size
                this.entities = modification.entities
                this.metadata = modification.metadata
                this.space = modification.space
            }

            val updatedBlock = blockRepository.updateBlock(originalBlock).getOrThrow()
            val translatedBlock = translateOptionally(updatedBlock, languageCode)
            val translatedBlockDto = blockDtoConverter.convertToDto(translatedBlock)

            sendBlockUpdatedEvent(originalBlockDto, translatedBlockDto)

            updatedBlock to translatedBlockDto
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

    private suspend fun checkBlockLimit(block: BlockEntity) {
        val space = block.space
        val count = blockRepository.countBlocksInSpace(space).getOrThrow()

        if (count >= SpaceEntity.SPACE_CAPACITY) {
            logger.w {
                "Space ${space.id} reached its capacity. Unable to process block creation"
            }

            throw UnprocessableEntityException("Space capacity reached")
        }
    }

    private suspend fun processBlockIndex(block: BlockEntity) {
        val space = block.space
        val index = block.index

        val latestIndex = blockRepository.findLatestBlockIndexInSpace(space).getOrNull()
            ?: if (index != 0) {
                throw BadRequestException("First block index must be 0")
            } else {
                0
            }

        logger.d {
            "Processing block index. index: $index; latestIndex: $latestIndex"
        }

        when {
            index == latestIndex + 1 -> {}
            index <= latestIndex -> {
                val blocksToUpdate =
                    blockRepository.findBlocksInSpaceWhichIndexIsGreaterOrEqualTo(space, index).getOrThrow()

                blocksToUpdate.forEach {
                    it.index++
                }

                blockRepository.updateBlocks(blocksToUpdate)
            }

            else -> {
                throw BadRequestException("Block index is out of bounds")
            }
        }
    }

    private suspend fun translateOptionally(block: BlockEntity, languageCode: LanguageCode?) =
        block.apply {
            languageCode?.let {
                translationService.translateBlock(block = this, languageCode = it)
            }
        }

    private suspend fun translateOptionally(blocks: List<BlockEntity>, languageCode: LanguageCode?) =
        blocks.map { block ->
            translateOptionally(block, languageCode)
        }

    private suspend fun sendBlockCreatedEvent(blockDto: Block) {
        val event = event(EventReferences.blockCreated) {
            blockDto
        }
        eventChannel.send(event)
    }

    private suspend fun sendBlockUpdatedEvent(oldBlockDto: Block, newBlockDto: Block) {
        val eventRef = EventReferences.blockUpdated(newBlockDto.id)
        val update = EntityUpdate(oldBlockDto, newBlockDto)
        val event = event(eventRef) {
            update
        }
        eventChannel.send(event)
    }

    private suspend fun sendBlockRemovedEvent(blockDto: Block) {
        val eventRef = EventReferences.blockRemoved(blockDto.id)
        val event = event(eventRef) {
            blockDto
        }
        eventChannel.send(event)
    }
}