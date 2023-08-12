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

package dev.d1s.beam.ui.util

import dev.d1s.beam.commons.BlockSize

object Size {

    private const val STEP = 225
    private const val WHITESPACE = 45

    private val LgBreakpoint = breakpointOf(BlockSize.LARGE)
    private val XlBreakpoint = breakpointOf(BlockSize.EXTRA_LARGE)

    val MaxBlockSize: BlockSize
        get() {
            val width = vw

            return when {
                width in LgBreakpoint..<XlBreakpoint -> BlockSize.LARGE
                width >= XlBreakpoint -> BlockSize.EXTRA_LARGE
                else -> BlockSize.MEDIUM
            }
        }

    val Sm = sizeOf(BlockSize.SMALL)
    val Md = sizeOf(BlockSize.MEDIUM)
    val Lg
        get() = when {
            MaxBlockSize.level >= BlockSize.LARGE.level -> sizeOf(BlockSize.LARGE)
            else -> Md
        }
    val Xl get() = sizeOf(MaxBlockSize)

    fun sizeOf(blockSize: BlockSize) =
        STEP * blockSize.level

    private fun breakpointOf(blockSize: BlockSize) =
        sizeOf(blockSize) + WHITESPACE
}