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
import dev.d1s.beam.commons.DaemonStatus
import io.kvision.state.ObservableValue
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext
import org.koin.core.time.measureDuration
import kotlin.time.Duration.Companion.seconds

data class DaemonStatusWithPing(
    val status: DaemonStatus,
    val ping: Int
)

interface DaemonConnector {

    val observableStatus: ObservableValue<DaemonStatusWithPing?>

    val observableStatusWithPing: ObservableValue<DaemonStatusWithPing?>

    suspend fun isUp(): Boolean?

    suspend fun getDaemonStatus(): DaemonStatusWithPing?

    fun launchMonitor(): Job
}

class DefaultDaemonConnector : DaemonConnector, KoinComponent {

    override val observableStatus = ObservableValue<DaemonStatusWithPing?>(null)

    override val observableStatusWithPing = ObservableValue<DaemonStatusWithPing?>(null)

    private val client by inject<PublicBeamClient>()

    private val monitoringScope = CoroutineScope(Dispatchers.Main)

    private var monitorLaunched = atomic(false)

    override suspend fun isUp(): Boolean? =
        (getDaemonStatus() != null).takeIf { monitorLaunched.value }

    override suspend fun getDaemonStatus(): DaemonStatusWithPing? {
        var status: DaemonStatus? = null

        val ping = measureDuration {
            status = client.getDaemonStatus().getOrNull()
        }

        return status?.let {
            DaemonStatusWithPing(it, ping.toInt())
        }
    }

    override fun launchMonitor(): Job {
        if (monitorLaunched.value) {
            error("Monitor has already been launched")
        }

        monitorLaunched.value = true

        return monitoringScope.launch {
            while (true) {
                val status = getDaemonStatus()

                if (observableStatus.value?.status != status?.status) {
                    observableStatus.value = status
                }

                if (observableStatusWithPing.value != status) {
                    observableStatusWithPing.value = status
                }

                delay(3.seconds)
            }
        }
    }
}

private val daemonConnector by lazy {
    GlobalContext.get().get<DaemonConnector>()
}

suspend fun DaemonStatusWithPing?.down() = this == null && daemonConnector.isUp() == false
