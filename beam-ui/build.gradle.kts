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

import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    id("io.kvision")
    kotlin("js")
}

dependencies {
    val exktVersion: String by project

    val kvisionVersion: String by project

    val catppuccinKotlinVersion: String by project

    val animateCssVersion: String by project

    val koinVersion: String by project

    val kotlinxCoroutinesVersion: String by project

    implementation(project(":beam-client-sdk"))

    implementation("dev.d1s.exkt:exkt-kvision:$exktVersion")
    implementation("dev.d1s.exkt:exkt-common:$exktVersion")

    implementation("io.kvision:kvision:$kvisionVersion")
    implementation("io.kvision:kvision-state:$kvisionVersion")
    implementation("io.kvision:kvision-bootstrap:$kvisionVersion")
    implementation("io.kvision:kvision-bootstrap-css:$kvisionVersion")
    implementation("io.kvision:kvision-bootstrap-icons:$kvisionVersion")
    implementation("io.kvision:kvision-routing-navigo-ng:$kvisionVersion")

    implementation("com.catppuccin:catppuccin-kotlin:$catppuccinKotlinVersion")

    implementation(npm("animate.css", animateCssVersion))

    implementation("io.insert-koin:koin-core:$koinVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
}

val webDir = file("src/main/web")

kotlin {
    js {
        browser {
            val outputFile = "main.bundle.js"

            runTask {
                outputFileName = outputFile
                sourceMaps = false
                devServer = KotlinWebpackConfig.DevServer(
                    open = false,
                    port = 8080,
                    static = mutableListOf("$buildDir/processedResources/js/main")
                )
            }

            webpackTask {
                outputFileName = outputFile
            }

            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }

        binaries.executable()
    }

    sourceSets["main"].resources.srcDir(webDir)
}