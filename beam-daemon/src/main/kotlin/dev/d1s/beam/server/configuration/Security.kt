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

package dev.d1s.beam.server.configuration

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.d1s.exkt.ktor.server.koin.configuration.ApplicationConfigurer
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import org.koin.core.module.Module

internal object Security : ApplicationConfigurer {

    val jwtAlgorithm get() = _jwtAlgorithm ?: error("Algorithm is not available")

    private var _jwtAlgorithm: Algorithm? = null

    override fun Application.configure(module: Module, config: ApplicationConfig) {
        _jwtAlgorithm = Algorithm.HMAC256(config.jwtSecret)

        authentication {
            jwt {
                realm = config.jwtRealm

                val verifier = JWT.require(jwtAlgorithm)
                    .withAudience(config.jwtAudience)
                    .withIssuer(config.jwtIssuer)
                    .build()

                verifier(verifier)

                validate { credential ->
                    val payload = credential.payload
                    val subject = payload.subject

                    if (subject != null && subject.isNotBlank()) {
                        JWTPrincipal(payload)
                    } else {
                        null
                    }
                }
            }
        }
    }
}

internal val ApplicationConfig.jwtRealm
    get() = property("jwt.realm").getString()

internal val ApplicationConfig.jwtSecret
    get() = property("jwt.secret").getString()

internal val ApplicationConfig.jwtAudience
    get() = property("jwt.audience").getString()

internal val ApplicationConfig.jwtIssuer
    get() = property("jwt.issuer").getString()

internal val ApplicationCall.jwtSubject
    get() = (principal<JWTPrincipal>() ?: error("No JWT principal")).payload.subject