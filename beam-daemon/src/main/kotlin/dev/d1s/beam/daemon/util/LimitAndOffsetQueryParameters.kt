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

package dev.d1s.beam.daemon.util

import dev.d1s.beam.commons.Paths
import io.ktor.server.application.*
import io.ktor.server.plugins.*

data class LimitAndOffsetQueryParameters(
    val limit: Int,
    val offset: Int
)

val ApplicationCall.requiredLimitAndOffsetQueryParameters: LimitAndOffsetQueryParameters
    get() {
        val query = request.queryParameters

        val limit = query[Paths.LIMIT_QUERY_PARAMETER]?.toUIntOrNull()
        val offset = query[Paths.OFFSET_QUERY_PARAMETER]?.toUIntOrNull()

        if (limit == null || offset == null) {
            throw BadRequestException("Valid ${Paths.LIMIT_QUERY_PARAMETER} and ${Paths.OFFSET_QUERY_PARAMETER} query parameters required. Got limit: $limit, offset: $offset")
        }

        return LimitAndOffsetQueryParameters(limit.toInt(), offset.toInt())
    }