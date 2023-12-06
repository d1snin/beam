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

import dev.d1s.beam.commons.*
import dev.d1s.beam.commons.contententity.ContentEntities
import dev.d1s.beam.commons.contententity.ContentEntityTypeName
import dev.d1s.beam.commons.contententity.definition
import dev.d1s.beam.commons.event.EntityUpdate
import dev.d1s.beam.commons.event.EventReferences
import dev.d1s.beam.daemon.configuration.DtoConverters
import dev.d1s.beam.daemon.database.BlockRepository
import dev.d1s.beam.daemon.entity.BlockEntities
import dev.d1s.beam.daemon.entity.BlockEntity
import dev.d1s.beam.daemon.entity.asString
import dev.d1s.beam.daemon.entity.requiredIndex
import dev.d1s.beam.daemon.service.contententity.ContentEntityMetadataProcessor
import dev.d1s.exkt.dto.DtoConverter
import dev.d1s.exkt.dto.ResultingEntityWithOptionalDto
import dev.d1s.exkt.dto.convertToDtoIf
import dev.d1s.exkt.dto.entity
import dev.d1s.exkt.ktorm.dto.ResultingExportedSequenceWithOptionalDto
import dev.d1s.exkt.ktorm.dto.convertExportedSequenceToDtoIf
import dev.d1s.ktor.events.server.WebSocketEventChannel
import dev.d1s.ktor.events.server.event
import io.ktor.server.plugins.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext
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
        limit: Int,
        offset: Int,
        languageCode: LanguageCode? = null,
        requireDto: Boolean = false
    ): ResultingExportedSequenceWithOptionalDto<BlockEntity, Block>

    suspend fun getAllBlocks(
        spaceIdentifier: SpaceIdentifier,
        languageCode: LanguageCode? = null
    ): Result<BlockEntities>

    suspend fun updateBlock(
        id: BlockId,
        modification: BlockEntity,
        languageCode: LanguageCode? = null
    ): ResultingEntityWithOptionalDto<BlockEntity, Block>

    suspend fun removeBlock(id: BlockId): Result<Unit>

    suspend fun removeAllBlocks(spaceIdentifier: SpaceIdentifier): Result<Unit>
}

class DefaultBlockService : BlockService, KoinComponent {

    private val blockRepository by inject<BlockRepository>()

    private val blockDtoConverter by inject<DtoConverter<BlockEntity, Block>>(DtoConverters.BlockDtoConverterQualifier)

    private val eventChannel by inject<WebSocketEventChannel>()

    private val rowService by inject<RowService>()

    private val spaceService by inject<SpaceService>()

    private val translationService by inject<TranslationService>()

    private val contentEntityMetadataProcessors by lazy {
        GlobalContext.get().getAll<ContentEntityMetadataProcessor>()
    }

    private val logger = logging()

    override suspend fun createBlock(
        block: BlockEntity,
        languageCode: LanguageCode?
    ): ResultingEntityWithOptionalDto<BlockEntity, Block> =
        blockRepository.withTransactionCatching {
            logger.d {
                "Creating block ${block.asString}..."
            }

            translationService.verifyLocationsExist(block).getOrThrow()

            processBlockRow(block)
            processBlockIndexOnCreation(block)

            val processedBlock = block.populateContentEntityMetadata()

            val addedBlock = blockRepository.addBlock(processedBlock).getOrThrow()
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
        limit: Int,
        offset: Int,
        languageCode: LanguageCode?,
        requireDto: Boolean
    ): ResultingExportedSequenceWithOptionalDto<BlockEntity, Block> =
        runCatching {
            logger.d {
                "Obtaining blocks for space with unique identifier $spaceIdentifier..."
            }

            val (space, _) = spaceService.getSpace(spaceIdentifier).getOrThrow()

            val blocks = blockRepository.findBlocksInSpace(space, limit, offset).getOrThrow()

            val translatedBlocks = translateOptionally(blocks.elements, languageCode)
            val translatedSequence = blocks.copy(elements = translatedBlocks)

            translatedSequence to blockDtoConverter.convertExportedSequenceToDtoIf(translatedSequence) {
                requireDto
            }
        }

    override suspend fun getAllBlocks(
        spaceIdentifier: SpaceIdentifier,
        languageCode: LanguageCode?
    ): Result<BlockEntities> =
        runCatching {
            val space = spaceService.getSpace(spaceIdentifier).getOrThrow().entity
            blockRepository.findAllBlocksInSpace(space).getOrThrow()
        }

    override suspend fun updateBlock(
        id: BlockId,
        modification: BlockEntity,
        languageCode: LanguageCode?
    ): ResultingEntityWithOptionalDto<BlockEntity, Block> =
        blockRepository.withTransactionCatching {
            logger.d {
                "Updating block with ID $id with data ${modification.asString}..."
            }

            val (originalBlock, originalBlockDto) = getBlock(id, requireDto = true).getOrThrow()
            requireNotNull(originalBlockDto)

            translationService.verifyLocationsExist(modification).getOrThrow()

            processBlockRow(modification)
            processBlockIndexOnUpdate(modification, originalBlock)

            originalBlock.apply {
                this.row = modification.row
                this.index = modification.index
                this.size = modification.size
                this.entities = populateContentEntityMetadata(modification.entities)
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
        blockRepository.withTransactionCatching {
            logger.d {
                "Removing block with ID $id..."
            }

            val (block, blockDto) = getBlock(id, requireDto = true).getOrThrow()
            requireNotNull(blockDto)

            processBlockIndexOnRemoval(block)

            blockRepository.removeBlock(block).getOrThrow()

            sendBlockRemovedEvent(blockDto)
        }

    override suspend fun removeAllBlocks(spaceIdentifier: SpaceIdentifier): Result<Unit> =
        blockRepository.withTransactionCatching {
            logger.d {
                "Removing all blocks associated with space '$spaceIdentifier'..."
            }

            val blocks = getAllBlocks(spaceIdentifier).getOrThrow()

            blocks.forEach { block ->
                removeBlock(block.id.toString()).getOrThrow()
            }
        }

    private suspend fun processBlockRow(block: BlockEntity) {
        rowService.createRowIfDoesntExist(block.row, block.space).getOrThrow()
    }

    private suspend fun processBlockIndexOnCreation(block: BlockEntity) {
        val (index, latestIndex) = prepareBlockIndexProcessing(block)

        val space = block.space
        val row = block.row

        when {
            index == latestIndex + 1 -> {}
            index <= latestIndex -> {
                updateBlocks(
                    fetch = {
                        blockRepository.findBlocksInSpaceByRowWhichIndexIsGreaterOrEqualTo(space, row, index)
                            .getOrThrow()
                    }
                ) {
                    it.index = it.requiredIndex + 1
                }
            }
        }
    }

    private suspend fun processBlockIndexOnUpdate(block: BlockEntity, originalBlock: BlockEntity) {
        val initialIndex = originalBlock.requiredIndex
        val (index, _) = prepareBlockIndexProcessing(block)

        val space = block.space
        val row = block.row

        when {
            initialIndex < index -> {
                updateBlocks(
                    fetch = {
                        blockRepository.findBlocksInSpaceByRowWhichIndexIsBetweenStartExclusive(
                            space,
                            row,
                            initialIndex,
                            index
                        ).getOrThrow()
                    }
                ) {
                    it.index = it.requiredIndex - 1
                }
            }

            initialIndex > index -> {
                updateBlocks(
                    fetch = {
                        blockRepository.findBlocksInSpaceByRowWhichIndexIsBetweenEndExclusive(
                            space,
                            row,
                            index,
                            initialIndex
                        ).getOrThrow()
                    }
                ) {
                    it.index = it.requiredIndex + 1
                }
            }
        }
    }

    private suspend fun processBlockIndexOnRemoval(block: BlockEntity) {
        val (index, _) = prepareBlockIndexProcessing(block)

        val space = block.space
        val row = block.row

        updateBlocks(
            fetch = {
                blockRepository.findBlocksInSpaceByRowWhichIndexIsGreater(space, row, index)
                    .getOrThrow()
            }
        ) {
            it.index = it.requiredIndex - 1
        }
    }

    private suspend fun prepareBlockIndexProcessing(block: BlockEntity): Pair<BlockIndex, BlockIndex> {
        val space = block.space
        val row = block.row

        var latestIndex = blockRepository.findLatestBlockIndexInSpaceByRow(space, row).getOrNull()

        if (block.index == null) {
            block.index = latestIndex?.inc() ?: START_INDEX
        }

        val requiredIndex = block.requiredIndex

        if (latestIndex == null) {
            if (requiredIndex != START_INDEX) {
                throw BadRequestException("First block index must be $START_INDEX")
            }

            latestIndex = START_INDEX
        }

        if (requiredIndex > (latestIndex + 1) || requiredIndex < START_INDEX) {
            throw BadRequestException("Block index is out of bounds")
        }

        logger.d {
            "Processing block index. index: $requiredIndex; latestIndex: $latestIndex"
        }

        return requiredIndex to latestIndex
    }

    private suspend fun updateBlocks(fetch: suspend () -> BlockEntities, onEach: suspend (BlockEntity) -> Unit) {
        val blocksToUpdate = fetch()

        blocksToUpdate.forEach {
            onEach(it)
        }

        blockRepository.updateBlocks(blocksToUpdate)
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

    private suspend fun BlockEntity.populateContentEntityMetadata() =
        apply {
            entities = populateContentEntityMetadata(entities)
        }

    private suspend fun populateContentEntityMetadata(entities: ContentEntities) =
        entities.map {
            var entity = it

            val optionalProcessor = contentEntityMetadataProcessorByType(entity.type)

            optionalProcessor?.let { processor ->
                val metadata = entity.metadata.toMutableMap()

                processor.populate(entity, metadata)

                entity = entity.copy(metadata = metadata)
            }

            entity
        }.also {
            logger.d {
                "Populated content entity metadata: $it"
            }
        }

    private fun contentEntityMetadataProcessorByType(typeName: ContentEntityTypeName): ContentEntityMetadataProcessor? {
        val definition = definition(typeName) ?: error("content entity definition not found")

        return contentEntityMetadataProcessors.find {
            it.type == definition
        }
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

    private companion object {

        private const val START_INDEX = 0
    }
}