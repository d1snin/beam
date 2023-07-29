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

import dev.d1s.beam.client.PublicBeamClient
import dev.d1s.beam.commons.Role
import dev.d1s.beam.commons.Space
import dev.d1s.exkt.common.pathname
import kotlinx.browser.window
import org.koin.core.context.GlobalContext

private val beamClient by lazy {
    GlobalContext.get().get<PublicBeamClient>()
}

suspend fun currentSpace(): Space? {
    val url = window.location.href
    return beamClient.resolver.resolve(url).getOrNull()
}

suspend fun isRootPath(): Boolean {
    currentSpace()?.let {
        return it.role == Role.ROOT
    }

    return pathname == "/" || pathname == "/root"
}