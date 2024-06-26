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

    js {
        browser {
            commonWebpackConfig(
                Action {
                    cssSupport {
                        enabled.set(true)
                    }
                }
            )
        }

        nodejs()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                val kotlinxSerializationVersion: String by project

                val ktorWsEventsVersion: String by project

                val exktVersion: String by project

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")

                implementation("dev.d1s.ktor-ws-events:ktor-ws-events-commons:$ktorWsEventsVersion")

                implementation("dev.d1s.exkt:exkt-konform:$exktVersion")
            }
        }
    }

    explicitApi()
}
