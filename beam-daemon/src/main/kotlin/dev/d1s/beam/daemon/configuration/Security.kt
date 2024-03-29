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

package dev.d1s.beam.daemon.configuration

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.d1s.beam.daemon.service.SpaceService
import dev.d1s.exkt.ktor.server.koin.configuration.ApplicationConfigurer
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.module.Module

object Security : ApplicationConfigurer, KoinComponent {

    val jwtAlgorithm get() = _jwtAlgorithm ?: error("Algorithm is not available")

    private var _jwtAlgorithm: Algorithm? = null

    private val spaceService by inject<SpaceService>()

    override fun Application.configure(module: Module, config: ApplicationConfig) {
        _jwtAlgorithm = Algorithm.HMAC256(config.jwtSecret)

        authentication {
            jwt {
                realm = config.jwtRealm

                val verifier = buildVerifier(config)

                verifier(verifier)

                validate()
            }
        }
    }

    private fun buildVerifier(config: ApplicationConfig) =
        JWT.require(jwtAlgorithm)
            .withIssuer(config.jwtIssuer)
            .build()

    private fun JWTAuthenticationProvider.Config.validate() {
        validate { credential ->
            val payload = credential.payload
            val subject = payload.subject

            if (
                subject != null
                && subject.isNotBlank()
                && spaceService.spaceExists(subject).getOrThrow()
            ) {
                JWTPrincipal(payload)
            } else {
                null
            }
        }
    }
}

val ApplicationCall.jwtSubject
    get() = (principal<JWTPrincipal>()
        ?: error("No JWT principal")).payload.subject
        ?: error("No JWT subject")

val ApplicationConfig.jwtRealm
    get() = property("security.jwt.realm").getString()

val ApplicationConfig.jwtSecret
    get() = property("security.jwt.secret").getString()


val ApplicationConfig.jwtIssuer
    get() = property("security.jwt.issuer").getString()