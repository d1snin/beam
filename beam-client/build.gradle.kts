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

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("maven-publish")
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
                val ktorVersion: String by project

                val ktorWsEventsVersion: String by project

                val exktVersion: String by project

                val kotlinxCoroutinesVersion: String by project

                api(project(":beam-common"))

                api("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

                api("dev.d1s.ktor-ws-events:ktor-ws-events-client:$ktorWsEventsVersion")

                implementation("dev.d1s.exkt:exkt-common:$exktVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
            }
        }

        val jvmMain by getting {
            dependencies {
                val ktorVersion: String by project

                implementation("io.ktor:ktor-client-cio:$ktorVersion")
            }
        }

        val jsMain by getting {
            dependencies {
                val ktorVersion: String by project

                implementation("io.ktor:ktor-client-js:$ktorVersion")
            }
        }
    }

    explicitApi()
}

publishing {
    repositories {
        maven {
            name = "mavenD1sDevRepository"

            val channel = if (isDevVersion) {
                "snapshots"
            } else {
                "releases"
            }

            url = uri("https://maven.d1s.dev/${channel}")

            credentials {
                username = System.getenv("MAVEN_D1S_DEV_USERNAME")
                password = System.getenv("MAVEN_D1S_DEV_PASSWORD")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            if (isDevVersion) {
                val commitShortSha = System.getenv("GIT_SHORT_COMMIT_SHA")

                commitShortSha?.let {
                    version = "$version-$it"
                }
            }
        }
    }
}

val isDevVersion get() = version.toString().endsWith("-dev")