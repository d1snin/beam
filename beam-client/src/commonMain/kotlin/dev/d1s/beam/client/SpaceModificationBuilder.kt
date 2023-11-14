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

@BuilderDsl
public class RootSpaceModificationBuilder {

    private var view: SpaceView? = null

    private val metadataBuilder = MetadataBuilder()

    public fun metadata(key: MetadataKey, value: MetadataValue) {
        metadataBuilder.metadata(key, value)
    }

    public fun view(build: SpaceViewBuilder.() -> Unit) {
        view = SpaceViewBuilder().apply(build).buildSpaceView()
    }

    public fun buildRootSpaceModification(): RootSpaceModification =
        RootSpaceModification(
            metadata = metadataBuilder.buildMetadata(),
            view ?: error("View is undefined")
        )
}

@BuilderDsl
public class SpaceModificationBuilder {

    public var slug: SpaceSlug? = null

    public var metadata: Metadata = metadataOf()

    private var view: SpaceView? = null

    public fun view(build: SpaceViewBuilder.() -> Unit) {
        view = SpaceViewBuilder().apply(build).buildSpaceView()
    }

    public fun buildSpaceModification(): SpaceModification =
        SpaceModification(
            slug ?: error("Space slug is undefined"),
            metadata,
            view ?: error("View is undefined")
        )
}

@BuilderDsl
public class SpaceViewBuilder {

    public var theme: SpaceThemeName? = null

    public var icon: SpaceIconUrl? = null

    public var title: SpaceTitle? = null

    public var description: SpaceDescription? = null

    public var remark: SpaceRemark? = null

    private var favicon: SpaceFavicon? = null

    private var preview: SpaceUrlPreview? = null

    public fun favicon(build: SpaceFaviconBuilder.() -> Unit) {
        favicon = SpaceFaviconBuilder().apply(build).buildSpaceFavicon()
    }

    public fun preview(build: SpaceUrlPreviewBuilder.() -> Unit) {
        preview = SpaceUrlPreviewBuilder().apply(build).buildSpaceUrlPreview()
    }

    public fun buildSpaceView(): SpaceView =
        SpaceView(
            theme ?: error("Space theme is undefined"),
            icon,
            favicon,
            preview,
            title,
            description,
            remark
        )
}

@BuilderDsl
public class SpaceFaviconBuilder {

    public var appleTouch: SpaceIconUrl? = null

    public var favicon16: SpaceIconUrl? = null

    public var favicon32: SpaceIconUrl? = null

    public var faviconIco: SpaceIconUrl? = null

    public fun buildSpaceFavicon(): SpaceFavicon =
        SpaceFavicon(
            appleTouch,
            favicon16,
            favicon32,
            faviconIco
        )
}

@BuilderDsl
public class SpaceUrlPreviewBuilder {

    public var type: SpaceUrlPreview.Type? = null

    public var image: Url? = null

    public fun buildSpaceUrlPreview(): SpaceUrlPreview =
        SpaceUrlPreview(
            type ?: error("Preview type is undefined"),
            image
        )
}