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

package dev.d1s.beam.commons.validation

import dev.d1s.beam.commons.SpaceSlug

private val blackList = mutableListOf<SpaceSlug>()

internal object SpaceSlugBlacklist : List<SpaceSlug> by blackList {
    init {
        blackList.apply {
            add("apple-touch-icon.png")
            add("browserconfig.xml")
            add("favicon.ico")
            add("favicon-16x16.png")
            add("favicon-32x32.png")
            add("icon.png")
            add("main.bundle.js")
            add("mstile-150x150.png")
            add("robots.txt")
            add("safari-pinned-tab.svg")
        }
    }
}