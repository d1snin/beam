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

package dev.d1s.beam.ui.client

import dev.d1s.beam.client.PublicBeamClient
import dev.d1s.beam.commons.DaemonConnectorMeta
import io.ktor.http.*
import kotlinx.browser.document

private const val DAEMON_DOCKER_HOST = "beam-daemon"

fun buildBeamClient(): PublicBeamClient {
    val connectorHttp = document.querySelector("meta[name=\"${DaemonConnectorMeta.HTTP}\"]")?.textContent
        ?: error("No ${DaemonConnectorMeta.HTTP} meta")

    val connectorWs = document.querySelector("meta[name=\"${DaemonConnectorMeta.WS}\"]")?.textContent
        ?: error("No ${DaemonConnectorMeta.WS} meta")

    val httpUrl = Url(connectorHttp)
    val wsUrl = Url(connectorWs)

    return PublicBeamClient(httpUrl.ensureCorrectUrl(), wsUrl.ensureCorrectUrl())
}

private fun Url.ensureCorrectUrl(): String {
    if (host == DAEMON_DOCKER_HOST) {
        val builder = URLBuilder(this)

        builder.set {
            host = "localhost"
        }

        return builder.buildString()
    }

    return this.toString()
}