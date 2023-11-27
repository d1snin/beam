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

package dev.d1s.beam.ui.contententity

import dev.d1s.beam.commons.MetadataKeys
import dev.d1s.beam.commons.contententity.ContentEntity
import dev.d1s.beam.commons.contententity.FileContentEntityTypeDefinition
import dev.d1s.beam.commons.contententity.get
import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.component.DescriptiveCardComponent
import dev.d1s.exkt.kvision.component.Component
import dev.d1s.exkt.kvision.component.render
import io.ktor.http.*
import io.kvision.html.icon
import io.kvision.panel.SimplePanel
import io.kvision.utils.rem
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.math.pow

class FileContentEntityRenderer : SingleContentEntityRenderer, KoinComponent {

    override val definition = FileContentEntityTypeDefinition

    override fun SimplePanel.render(context: SingleContentEntityRenderingContext) {
        val entity = context.entity
        val parameters = entity.parameters

        val url = parameters[definition.url]
        requireNotNull(url)

        val preferredFileName = parameters[definition.fileName]
        val fileName = preferredFileName ?: Url(url).pathSegments.lastOrNull() ?: DEFAULT_FILE_NAME

        val descriptiveCardComponent =
            get<Component<DescriptiveCardComponent.Config>>(Qualifier.DescriptiveCardComponent)

        render(descriptiveCardComponent) {
            setTitle(fileName)
            setDescription(entity)
            setUrl(url, fileName)
            setCardFullWidth()
            setImage()
        }
    }

    private fun DescriptiveCardComponent.Config.setTitle(fileName: String) {
        title.value = fileName
    }

    private fun DescriptiveCardComponent.Config.setDescription(entity: ContentEntity) {
        val size = entity.metadata[MetadataKeys.FILE_CONTENT_ENTITY_SIZE]?.toLongOrNull()
        val formattedSize = size?.formatByteLength()

        formattedSize?.let {
            description.value = it
        }
    }

    private fun DescriptiveCardComponent.Config.setUrl(fileUrl: String, fileName: String) {
        url.value = fileUrl
        external.value = true
        download.value = true
        downloadName.value = fileName
    }

    private fun DescriptiveCardComponent.Config.setCardFullWidth() {
        fullWidth.value = true
    }

    private fun DescriptiveCardComponent.Config.setImage() {
        image {
            fontSize = 2.0.rem
            icon("bi bi-download")
        }
    }

    private fun Long.formatByteLength(): String {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var size = toDouble()
        var unitIndex = 0

        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }

        val formattedSize = if (size % 1 == 0.0) {
            size.toInt().toString()
        } else {
            val decimalPlaces = 2
            val multiplier = 10.0.pow(decimalPlaces)
            val roundedSize = kotlin.math.round(size * multiplier) / multiplier
            roundedSize.toString()
        }

        return "$formattedSize ${units[unitIndex]}"
    }

    private companion object {

        private const val DEFAULT_FILE_NAME = "file"
    }
}