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

package dev.d1s.beam.bundle.configuration

import dev.d1s.beam.bundle.html.*
import dev.d1s.exkt.ktor.server.koin.configuration.ApplicationConfigurer
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named

object IndexModules : ApplicationConfigurer {

    private val CommonHeadParametersIndexModuleQualifier = named("common-head-parameters-index-module")
    private val UrlPreviewIndexModuleQualifier = named("url-preview-index-module")
    private val IconIndexModule = named("icon-index-module")
    private val CounterScriptIndexModule = named("counter-script-index-module")

    override fun Application.configure(module: Module, config: ApplicationConfig) {
        module.apply {
            singleOf<IndexModule>(::CommonHeadParametersIndexModule) {
                qualifier = CommonHeadParametersIndexModuleQualifier
            }

            singleOf<IndexModule>(::UrlPreviewIndexModule) {
                qualifier = UrlPreviewIndexModuleQualifier
            }

            singleOf<IndexModule>(::IconIndexModule) {
                qualifier = IconIndexModule
            }

            singleOf<IndexModule>(::CounterScriptIndexModule) {
                qualifier = CounterScriptIndexModule
            }
        }
    }
}