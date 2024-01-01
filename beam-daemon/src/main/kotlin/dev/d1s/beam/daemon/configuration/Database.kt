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

package dev.d1s.beam.daemon.configuration

import com.zaxxer.hikari.HikariDataSource
import dev.d1s.exkt.ktor.server.koin.configuration.ApplicationConfigurer
import dev.d1s.exkt.ktor.server.requiredJdbcProperties
import dev.d1s.ktor.liquibase.plugin.LiquibaseMigrations
import dev.d1s.ktor.liquibase.plugin.changeLogPath
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.core.module.Module
import org.ktorm.database.Database
import org.ktorm.support.postgresql.PostgreSqlDialect
import javax.sql.DataSource

object Database : ApplicationConfigurer {

    override fun Application.configure(module: Module, config: ApplicationConfig) {
        val hikariDataSource = initDataSource(config)
        val database = connect(hikariDataSource)

        migrate(database, config)

        module.single {
            database
        }
    }

    private fun initDataSource(config: ApplicationConfig) =
        HikariDataSource().apply {
            config.requiredJdbcProperties.let {
                jdbcUrl = it.url
                username = it.user
                password = it.password
            }
        }

    private fun connect(dataSource: DataSource) =
        Database.connect(
            dataSource = dataSource,
            dialect = PostgreSqlDialect()
        )

    private fun Application.migrate(database: Database, config: ApplicationConfig) {
        database.useConnection {
            install(LiquibaseMigrations) {
                changeLogPath = config.changeLogPath
                connection = it
            }
        }
    }
}