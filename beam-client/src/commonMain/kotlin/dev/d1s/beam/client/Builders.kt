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

package dev.d1s.beam.client

import dev.d1s.beam.commons.*

@DslMarker
internal annotation class BuilderDsl

@BuilderDsl
public class RootSpaceModificationBuilder {

    private var view: ViewConfiguration? = null

    public fun view(build: ViewConfigurationBuilder.() -> Unit) {
        view = ViewConfigurationBuilder().apply(build).build()
    }

    internal fun build() =
        RootSpaceModification(
            view ?: error("View is undefined")
        )
}

@BuilderDsl
public class SpaceModificationBuilder {

    public var slug: SpaceSlug? = null

    public var metadata: Metadata? = null

    private var view: ViewConfiguration? = null

    public fun view(build: ViewConfigurationBuilder.() -> Unit) {
        view = ViewConfigurationBuilder().apply(build).build()
    }

    internal fun build() =
        SpaceModification(
            slug ?: error("Space slug is undefined"),
            metadata ?: error("Space metadata is undefined"),
            view ?: error("View is undefined")
        )
}

@BuilderDsl
public class ViewConfigurationBuilder {

    public var theme: SpaceThemeName? = null

    public var icon: SpaceIconUrl? = null

    private var favicon: SpaceFavicon? = null

    public var title: SpaceTitle? = null

    public var description: SpaceDescription? = null

    public fun favicon(build: SpaceFaviconBuilder.() -> Unit) {
        favicon = SpaceFaviconBuilder().apply(build).build()
    }

    internal fun build() =
        ViewConfiguration(
            theme ?: error("Space theme is undefined"),
            icon,
            favicon,
            title,
            description
        )
}

@BuilderDsl
public class SpaceFaviconBuilder {

    public var appleTouch: SpaceIconUrl? = null

    public var favicon16: SpaceIconUrl? = null

    public var favicon32: SpaceIconUrl? = null

    public var faviconIco: SpaceIconUrl? = null

    public var browserconfig: Url? = null

    public var maskIcon: SpaceIconUrl? = null

    public var maskIconColor: String? = null

    internal fun build() =
        SpaceFavicon(
            appleTouch,
            favicon16,
            favicon32,
            faviconIco,
            browserconfig,
            maskIcon,
            maskIconColor
        )
}