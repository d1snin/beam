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

val ApplicationCall.requiredIdParameter: String
    get() = requiredParameter(Paths.ID_PARAMETER)

val ApplicationCall.requiredIntIdParameter: Int
    get() = requiredIdParameter.toIntOrNull() ?: throw BadRequestException("Invalid parameter. Integer expected")

val ApplicationCall.requiredLanguageCodeParameter: String
    get() = requiredParameter(Paths.LANGUAGE_CODE_PARAMETER)

val ApplicationCall.spaceIdQueryParameter: String?
    get() = request.queryParameters[Paths.SPACE_ID_QUERY_PARAMETER]

val ApplicationCall.requiredSpaceIdQueryParameter: String
    get() = requiredQueryParameter(Paths.SPACE_ID_QUERY_PARAMETER)

val ApplicationCall.languageCodeQueryParameter: String?
    get() = request.queryParameters[Paths.LANGUAGE_CODE_QUERY_PARAMETER]

val ApplicationCall.requiredLanguageCodeQueryParameter: String
    get() = requiredQueryParameter(Paths.LANGUAGE_CODE_QUERY_PARAMETER)

private fun ApplicationCall.requiredParameter(parameter: String) =
    requiredValue(parameters[parameter], message = "Parameter '$parameter' not found")

private fun ApplicationCall.requiredQueryParameter(parameter: String) =
    requiredValue(request.queryParameters[parameter], message = "Query parameter '$parameter' not found")

private fun requiredValue(value: String?, message: String) =
    value ?: throw BadRequestException(message)