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

package dev.d1s.beam.ui.client

import dev.d1s.beam.client.BeamClient
import dev.d1s.beam.commons.DaemonStatus
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.time.measureDuration

data class DaemonStatusWithPing(
    val daemonStatus: DaemonStatus,
    val ping: Int
)

interface DaemonConnector {

    suspend fun getDaemonStatus(): DaemonStatusWithPing?
}

class DefaultDaemonConnector : DaemonConnector, KoinComponent {

    private val client by inject<BeamClient>()

    override suspend fun getDaemonStatus(): DaemonStatusWithPing? {
        var status: DaemonStatus? = null

        val ping = measureDuration {
            status = client.getDaemonStatus().getOrNull()
        }

        return status?.let {
            DaemonStatusWithPing(it, ping.toInt())
        }
    }
}