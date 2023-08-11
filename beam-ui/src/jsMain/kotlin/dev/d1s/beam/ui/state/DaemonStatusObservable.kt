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

package dev.d1s.beam.ui.state

import dev.d1s.beam.ui.Qualifier
import dev.d1s.beam.ui.client.DaemonStatusWithPing
import io.kvision.state.ObservableValue
import kotlinx.coroutines.Job
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DaemonStatusObservable : Observable<DaemonStatusWithPing?>, KoinComponent {

    override val state = ObservableValue<DaemonStatusWithPing?>(null)

    private val daemonStatusWithPingObservable by inject<Observable<DaemonStatusWithPing?>>(Qualifier.DaemonStatusWithPingObservable)

    override fun monitor(): Job =
        launchMonitor {
            daemonStatusWithPingObservable.state.subscribe { statusWithPing ->
                if (state.value?.daemonStatus != statusWithPing?.daemonStatus) {
                    state.value = statusWithPing
                }
            }
        }
}