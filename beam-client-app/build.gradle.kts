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

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

apply {
    val publishingScript: String by project

    from(publishingScript)
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_17.majorVersion
        }

        withJava()
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                val kmLogVersion: String by project

                val hopliteVersion: String by project

                val koinVersion: String by project

                val dispatchVersion: String by project

                api(project(":beam-client"))

                implementation("org.lighthousegames:logging:$kmLogVersion")

                api("com.sksamuel.hoplite:hoplite-core:$hopliteVersion")

                api("io.insert-koin:koin-core:$koinVersion")
                implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

                api("com.sksamuel.hoplite:hoplite-core:$hopliteVersion")

                implementation("com.rickbusarow.dispatch:dispatch-core:$dispatchVersion")
            }
        }
    }

    explicitApi()
}