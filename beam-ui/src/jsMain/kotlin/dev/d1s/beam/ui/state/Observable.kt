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

package dev.d1s.beam.ui.state

import io.kvision.state.ObservableState
import kotlinx.coroutines.*

interface Observable<TState> {

    val state: ObservableState<TState>

    fun monitor(): Job
}

fun launchMonitor(loop: Boolean = false, block: suspend () -> Unit) =
    CoroutineScope(Dispatchers.Main).launch {
        if (loop) {
            while (isActive) {
                block()
            }
        } else {
            block()
        }
    }