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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    val exktVersion: String by project

    val ktorVersion: String by project
    val ktorServerLiquibaseVersion: String by project
    val ktorWsEventsVersion: String by project
    val ktorStaticAuthVersion: String by project

    val logbackVersion: String by project
    val kmLogVersion: String by project
    val telegramLogbackVersion: String by project

    val hikariVersion: String by project
    val postgresqlVersion: String by project
    val ktormVersion: String by project

    val koinVersion: String by project

    val dispatchVersion: String by project

    implementation("dev.d1s.exkt:exkt-common:$exktVersion")
    implementation("dev.d1s.exkt:exkt-ktor-server:$exktVersion")
    implementation("dev.d1s.exkt:exkt-ktor-server-koin:$exktVersion")
    implementation("dev.d1s.exkt:exkt-ktorm:$exktVersion")
    implementation("dev.d1s.exkt:exkt-konform:$exktVersion")

    implementation(project(":beam-common"))
    implementation(project(":beam-client"))
    implementation(project(":beam-bundle"))

    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-rate-limit:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("dev.d1s.exkt:exkt-ktor-server:$exktVersion")
    implementation("dev.d1s.exkt:exkt-ktor-server-postgres-support:$exktVersion")
    implementation("dev.d1s:ktor-server-liquibase:$ktorServerLiquibaseVersion")
    implementation("dev.d1s.ktor-ws-events:ktor-ws-events-server:$ktorWsEventsVersion")
    implementation("dev.d1s:ktor-static-authentication:$ktorStaticAuthVersion")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.lighthousegames:logging:$kmLogVersion")
    implementation("com.github.paolodenti:telegram-logback:$telegramLogbackVersion")

    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("org.ktorm:ktorm-core:$ktormVersion")
    implementation("org.ktorm:ktorm-support-postgresql:$ktormVersion")
    implementation("org.ktorm:ktorm-jackson:$ktormVersion")

    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

    implementation("com.rickbusarow.dispatch:dispatch-core:$dispatchVersion")
}

application {
    mainClass.set("dev.d1s.beam.daemon.MainKt")
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
    from(
        "../config/daemon.conf",
        "../config/bundle.conf"
    )
    into("src/main/resources")
}

tasks["processResources"].dependsOn(tasks["grabConfig"])

tasks.register<Delete>("cleanConfig") {
    delete(
        "src/main/resources/daemon.conf",
        "src/main/resources/bundle.conf"
    )
}

tasks["clean"].dependsOn(tasks["cleanConfig"])

tasks.register<Copy>("grabDbChangelog") {
    from("../config/db/changelog.json")
    into("src/main/resources")
}

tasks["processResources"].dependsOn(tasks["grabDbChangelog"])

tasks.register<Delete>("cleanDbChangelog") {
    delete("src/main/resources/changelog.json")
}

tasks["clean"].dependsOn(tasks["cleanDbChangelog"])

tasks.register<Copy>("grabResources") {
    from(
        "../static/apple-touch-icon.png",
        "../static/favicon.ico",
        "../static/favicon-16x16.png",
        "../static/favicon-32x32.png",
        "../static/icon.png",
        "../static/robots.txt",
        "../static/404_light.svg",
        "../static/404_dark.svg",
        "../static/empty_space_light.svg",
        "../static/empty_space_dark.svg",
        "../static/lost_connection_light.svg",
        "../static/lost_connection_dark.svg"
    )
    into("src/main/resources/static")
}

tasks["processResources"].dependsOn(tasks["grabResources"])

tasks.register<Delete>("cleanResources") {
    delete(
        "src/main/resources/static/404_light.svg",
        "src/main/resources/static/404_dark.svg",
        "src/main/resources/static/empty_space_light.svg",
        "src/main/resources/static/empty_space_dark.svg",
        "src/main/resources/static/lost_connection_light.svg",
        "src/main/resources/static/lost_connection_dark.svg"
    )
}

tasks["clean"].dependsOn(tasks["cleanResources"])