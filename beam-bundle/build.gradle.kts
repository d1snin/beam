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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    id("io.ktor.plugin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    val exktVersion: String by project

    val ktorVersion: String by project

    val logbackVersion: String by project
    val kmLogVersion: String by project

    val koinVersion: String by project

    implementation(project(":beam-client"))

    implementation("dev.d1s.exkt:exkt-common:$exktVersion")
    implementation("dev.d1s.exkt:exkt-ktor-server:$exktVersion")
    implementation("dev.d1s.exkt:exkt-ktor-server-koin:$exktVersion")

    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.lighthousegames:logging:$kmLogVersion")

    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")
}

application {
    mainClass.set("dev.d1s.beam.bundle.MainKt")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
    }
}

ktor {
    docker {
        localImageName.set(project.name)
    }
}

tasks.register<Copy>("grabJs") {
    from("../beam-ui/build/dist/js/productionExecutable/main.bundle.js")
    into("src/main/resources/static")
}

tasks["processResources"].dependsOn(tasks["grabJs"])

tasks.register<Delete>("cleanJs") {
    delete("src/main/resources/static/main.bundle.js")
}

tasks["clean"].dependsOn(tasks["cleanJs"])

tasks.register<Copy>("grabConfig") {
    from("../config/bundle.conf")
    into("src/main/resources")
}

tasks["processResources"].dependsOn(tasks["grabConfig"])

tasks.register<Delete>("cleanConfig") {
    delete("src/main/resources/bundle.conf")
}

tasks["clean"].dependsOn(tasks["cleanConfig"])

tasks.register<Copy>("grabResources") {
    from(
        "../img/404_light.svg",
        "../img/404_dark.svg",
        "../img/empty_space_light.svg",
        "../img/empty_space_dark.svg"
    )
    into("src/main/resources/static")
}

tasks["processResources"].dependsOn(tasks["grabResources"])

tasks.register<Delete>("cleanResources") {
    delete(
        "src/main/resources/static/404_light.svg",
        "src/main/resources/static/404_dark.svg",
        "src/main/resources/static/empty_space_light.svg",
        "src/main/resources/static/empty_space_dark.svg"
    )
}

tasks["clean"].dependsOn(tasks["cleanResources"])