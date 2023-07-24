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

package dev.d1s.beam.commons

import kotlinx.serialization.Serializable

public typealias Url = String
public typealias SpaceIconUrl = Url

public typealias SpaceTitle = String
public typealias SpaceDescription = String

@Serializable
public data class ViewConfiguration(
    val theme: SpaceThemeName,
    val icon: SpaceIconUrl?,
    val favicon: SpaceFavicon?,
    val title: SpaceTitle?,
    val description: SpaceDescription?
)

@Serializable
public data class SpaceFavicon(
    val appleTouch: SpaceIconUrl?,
    val favicon16: SpaceIconUrl?,
    val favicon32: SpaceIconUrl?,
    val faviconIco: SpaceIconUrl?,
    val browserconfig: Url?,
    val maskIcon: SpaceIconUrl?,
    val maskIconColor: String?
)