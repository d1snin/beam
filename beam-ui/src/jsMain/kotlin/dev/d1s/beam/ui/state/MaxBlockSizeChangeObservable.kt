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

import dev.d1s.beam.commons.BlockSize
import dev.d1s.beam.ui.util.Size
import io.kvision.state.ObservableValue
import kotlinx.browser.window
import org.koin.core.component.KoinComponent

class MaxBlockSizeChangeObservable : Observable<BlockSize>, KoinComponent {

    override val state = ObservableValue(Size.MaxBlockSize)

    override fun monitor() =
        launchMonitor(loop = false) {
            onResize {
                val maxBlockSize = Size.MaxBlockSize

                if (state.value != maxBlockSize) {
                    state.setState(maxBlockSize)
                }
            }
        }

    private inline fun onResize(crossinline block: () -> Unit) =
        window.addEventListener("resize", { _ -> block() }, true)
}