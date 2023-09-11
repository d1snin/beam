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

package dev.d1s.beam.client.app

import dev.d1s.beam.client.BeamClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging

internal interface ApplicationRunner {

    fun run(application: BeamClientApplication)
}

internal class DefaultApplicationRunner : ApplicationRunner {

    private val log = logging()

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun run(application: BeamClientApplication) {
        log.i {
            "Starting Beam client application..."
        }

        val applicationContext = createApplicationContext(application)

        with(application) {
            scope.launch {
                applicationContext.run()
            }
        }

        log.i {
            "Nothing to do anymore"
        }
    }

    private fun createApplicationContext(application: BeamClientApplication): ApplicationContext {
        val config = application.config

        val client = BeamClient(
            httpBaseUrl = config.httpBaseUrl,
            token = config.token
        )

        return ApplicationContext(config, client, scope)
    }
}

public fun run(application: BeamClientApplication) {
    val runner: ApplicationRunner = DefaultApplicationRunner()
    runner.run(application)
}