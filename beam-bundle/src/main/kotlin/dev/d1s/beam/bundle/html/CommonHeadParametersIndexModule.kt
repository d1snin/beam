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

package dev.d1s.beam.bundle.html

import dev.d1s.beam.bundle.configuration.daemonHttpAddress
import dev.d1s.beam.bundle.configuration.daemonWsAddress
import dev.d1s.beam.commons.DaemonConnectorMeta
import dev.d1s.beam.commons.MetadataKeys
import io.ktor.server.config.*
import kotlinx.html.HEAD
import kotlinx.html.meta
import kotlinx.html.title
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CommonHeadParametersIndexModule : IndexModule, KoinComponent {

    private val config by inject<ApplicationConfig>()

    override fun HEAD.render(renderParameters: RenderParameters) {
        title(renderParameters.title)
        meta("title", renderParameters.title)

        renderParameters.description?.let {
            meta("description", it)
        }

        val keywords = renderParameters.space?.metadata?.get(MetadataKeys.BUNDLE_SPACE_KEYWORDS)
        if (!keywords.isNullOrBlank()) {
            meta("keywords", keywords)
        }

        meta("charset", "utf-8")
        meta("viewport", "width=device-width, initial-scale=1")

        meta(DaemonConnectorMeta.HTTP, config.daemonHttpAddress)
        meta(DaemonConnectorMeta.WS, config.daemonWsAddress)
    }
}