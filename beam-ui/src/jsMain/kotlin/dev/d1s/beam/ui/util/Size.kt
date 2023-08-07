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
import io.kvision.core.CssSize
import io.kvision.utils.px
import kotlinx.browser.document
import kotlinx.browser.window
import kotlin.math.max

object Size {

    private const val STEP = 225
    private const val WHITESPACE = 45

    val MdBreakpoint = breakpointOf(BlockLevel.MD)
    val LgBreakpoint = breakpointOf(BlockLevel.LG)
    val XlBreakpoint = breakpointOf(BlockLevel.XL)

    val MaxBlockLevel: BlockLevel?
        get() {
            val width = vw

            return when {
                width >= BlockLevel.MD.breakpoint && width < BlockLevel.LG.breakpoint -> BlockLevel.MD
                width >= BlockLevel.LG.breakpoint && width < BlockLevel.XL.breakpoint -> BlockLevel.LG
                width >= BlockLevel.XL.breakpoint -> BlockLevel.XL
                else -> null
            }
        }

    val Sm = sizeOf(BlockLevel.SM).px
    val Md = sizeOf(BlockLevel.MD).px
    val Lg
        get() = when {
            vw >= BlockLevel.LG.breakpoint -> sizeOf(BlockLevel.LG).px
            else -> Md
        }
    val Xl
        get() = when {
            vw >= BlockLevel.XL.breakpoint -> sizeOf(BlockLevel.XL).px
            else -> Lg
        }

    private fun breakpointOf(blockLevel: BlockLevel) =
        sizeOf(blockLevel) + WHITESPACE

    private fun sizeOf(blockLevel: BlockLevel) =
        STEP * blockLevel.level

    private val vw get() = max(document.documentElement?.clientWidth ?: 0, window.innerWidth)
}

enum class BlockLevel(val level: Int, val breakpoint: Int, val size: BlockSize) {
    SM(level = 1, breakpoint = Size.MdBreakpoint, size = BlockSize.SMALL),
    MD(level = 2, breakpoint = Size.MdBreakpoint, size = BlockSize.MEDIUM),
    LG(level = 3, breakpoint = Size.LgBreakpoint, size = BlockSize.LARGE),
    XL(level = 4, breakpoint = Size.XlBreakpoint, size = BlockSize.EXTRA_LARGE)
}

val CssSize.int get() = first.toInt()