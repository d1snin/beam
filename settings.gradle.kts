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

rootProject.name = "sapphire"

pluginManagement {
    plugins {
        val kotlinVersion: String by settings

        val ktorVersion: String by settings

        val kvisionVersion: String by settings

        val versionsPluginVersion: String by settings

        kotlin("multiplatform") version kotlinVersion
        kotlin("jvm") version kotlinVersion
        kotlin("js") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion

        id("io.ktor.plugin") version ktorVersion

        id("io.kvision") version kvisionVersion

        id("com.github.ben-manes.versions") version versionsPluginVersion
    }
}

include(
    "sapphire-client-sdk",
    "sapphire-commons",
    "sapphire-server",
    "sapphire-ui"
)