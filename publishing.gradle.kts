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

apply {
    plugin("java-library")
    plugin("maven-publish")
}

configure<PublishingExtension> {
    repositories {
        maven {
            name = "mavenD1sDevRepository"

            val channel = if (isDevVersion) {
                "snapshots"
            } else {
                "releases"
            }

            url = uri("https://maven.d1s.dev/$channel")

            credentials {
                username = System.getenv("MAVEN_D1S_DEV_USERNAME")
                password = System.getenv("MAVEN_D1S_DEV_PASSWORD")
            }
        }
    }

    publications {
        if (isDevVersion) {
            val commitShortSha = System.getenv("GIT_SHORT_COMMIT_SHA")

            commitShortSha?.let {
                version = "$version-$it"
            }
        }
    }
}

val Project.isDevVersion get() = this.version.toString().endsWith("-dev")