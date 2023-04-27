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

val projectGroup: String by project
val projectVersion: String by project

group = projectGroup
version = projectVersion

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    val exktVersion: String by project

    val ktorVersion: String by project
    val ktorStaticAuthVersion: String by project
    val ktorServerLiquibaseVersion: String by project
    val ktorWsEventsVersion: String by project

    val logbackVersion: String by project
    val kmLogVersion: String by project

    val hikariVersion: String by project
    val postgresqlVersion: String by project
    val ktormVersion: String by project

    val koinVersion: String by project

    val dispatchVersion: String by project

    implementation("dev.d1s.exkt:exkt-common:$exktVersion")
    implementation("dev.d1s.exkt:exkt-ktor-server:$exktVersion")
    implementation("dev.d1s.exkt:exkt-ktor-server-koin:$exktVersion")
    implementation("dev.d1s.exkt:exkt-ktorm:$exktVersion")
    implementation("dev.d1s.exkt:exkt-konform-jvm:$exktVersion")

    implementation(project(":sapphire-commons"))

    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-rate-limit:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("dev.d1s:ktor-static-authentication:$ktorStaticAuthVersion")
    implementation("dev.d1s:ktor-server-liquibase:$ktorServerLiquibaseVersion")
    implementation("dev.d1s.ktor-ws-events:ktor-ws-events-server:$ktorWsEventsVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.lighthousegames:logging:$kmLogVersion")

    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("org.ktorm:ktorm-core:$ktormVersion")
    implementation("org.ktorm:ktorm-support-postgresql:$ktormVersion")


    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

    implementation("com.rickbusarow.dispatch:dispatch-core:$dispatchVersion")
}

application {
    mainClass.set("dev.d1s.sapphire.server.MainKt")
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