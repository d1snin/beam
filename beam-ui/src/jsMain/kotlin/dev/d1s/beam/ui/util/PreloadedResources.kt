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

package dev.d1s.beam.ui.util

import dev.d1s.beam.ui.resource.ResourceLocation
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.*

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object PreloadedResources : KoinComponent {

    private val httpClient by inject<HttpClient>()

    private lateinit var lostConnectionImageLightBase64: String

    private lateinit var lostConnectionImageDarkBase64: String

    val LostConnectionImageLightBase64
        get() = lostConnectionImageLightBase64

    val LostConnectionImageDarkBase64
        get() = lostConnectionImageDarkBase64

    suspend fun init() {
        lostConnectionImageLightBase64 = loadBase64(ResourceLocation.LOST_CONNECTION_LIGHT)
        lostConnectionImageDarkBase64 = loadBase64(ResourceLocation.LOST_CONNECTION_DARK)
    }

    private suspend fun loadBase64(resource: String) =
        httpClient.get(resource).readBytes().encodeBase64()
}