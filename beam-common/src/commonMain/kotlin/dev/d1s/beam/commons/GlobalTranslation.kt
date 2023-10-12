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

public object GlobalTranslation {

    public const val LOCATION_ICON_ALT: TextLocation = "ui.icon-alt"

    public const val LOCATION_DEFAULT_TITLE: TextLocation = "ui.default-title"
    public const val LOCATION_DEFAULT_REMARK: TextLocation = "ui.default-remark"

    public const val LOCATION_EXPLORE_DROPDOWN_CALLOUT: TextLocation = "ui.explore-dropdown.callout"

    public const val LOCATION_DAEMON_STATUS_CONNECTED: TextLocation = "ui.daemon-status.connected"
    public const val LOCATION_DAEMON_STATUS_DISCONNECTED: TextLocation = "ui.daemon-status.disconnected"
    public const val LOCATION_DAEMON_STATUS_MS_UNIT: TextLocation = "ui.daemon-status.ms-unit"

    public const val LOCATION_SPACE_FAILURE_CARD_NOT_FOUND_ICON_ALT: TextLocation =
        "ui.space-failure-card.not-found.icon-alt"
    public const val LOCATION_SPACE_FAILURE_CARD_NOT_FOUND_MESSAGE: TextLocation =
        "ui.space-failure-card.not-found.message"

    public const val LOCATION_SPACE_FAILURE_CARD_EMPTY_SPACE_ICON_ALT: TextLocation =
        "ui.space-failure-card.empty-space.icon-alt"
    public const val LOCATION_SPACE_FAILURE_CARD_EMPTY_SPACE_MESSAGE: TextLocation =
        "ui.space-failure-card.empty-space.message"

    public const val LOCATION_FOOTER_MESSAGE_FIRST_PART: TextLocation = "ui.footer.message.first-part"
    public const val LOCATION_FOOTER_MESSAGE_SECOND_PART: TextLocation = "ui.footer.message.second-part"

    public const val LOCATION_FOOTER_SOURCE_CODE_LINK_MESSAGE: TextLocation = "ui.footer.source-code-link.message"
    public const val LOCATION_FOOTER_SOURCE_CODE_LINK_URL: TextLocation = "ui.footer.source-code-link.url"

    public const val LOCATION_FOOTER_LANGUAGE_SWITCHER_MESSAGE: TextLocation = "ui.footer.language-switcher.message"

    public const val LOCATION_SPACE_LISTING_MESSAGE: TextLocation = "ui.space-listing.message"
    public const val LOCATION_SPACE_LISTING_FETCH_MORE_BUTTON: TextLocation = "ui.space-listing.fetch-more-button"

    public val Locations: List<TextLocation> = listOf(
        LOCATION_ICON_ALT,
        LOCATION_DEFAULT_TITLE,
        LOCATION_EXPLORE_DROPDOWN_CALLOUT,
        LOCATION_DAEMON_STATUS_CONNECTED,
        LOCATION_DAEMON_STATUS_DISCONNECTED,
        LOCATION_DAEMON_STATUS_MS_UNIT,
        LOCATION_SPACE_FAILURE_CARD_NOT_FOUND_ICON_ALT,
        LOCATION_SPACE_FAILURE_CARD_NOT_FOUND_MESSAGE,
        LOCATION_SPACE_FAILURE_CARD_EMPTY_SPACE_ICON_ALT,
        LOCATION_SPACE_FAILURE_CARD_EMPTY_SPACE_MESSAGE,
        LOCATION_FOOTER_MESSAGE_FIRST_PART,
        LOCATION_FOOTER_MESSAGE_SECOND_PART,
        LOCATION_FOOTER_SOURCE_CODE_LINK_MESSAGE,
        LOCATION_FOOTER_SOURCE_CODE_LINK_URL,
        LOCATION_FOOTER_LANGUAGE_SWITCHER_MESSAGE,
        LOCATION_SPACE_LISTING_MESSAGE,
        LOCATION_SPACE_LISTING_FETCH_MORE_BUTTON
    )

    public val Default: Translation = Translation(
        space = null,
        languageCode = "en",
        languageName = "English",
        default = false,
        translations = mapOf(
            LOCATION_ICON_ALT to "Beam space icon",
            LOCATION_DEFAULT_TITLE to "Beam",
            LOCATION_DEFAULT_REMARK to "Running [Beam][https://github.com/d1snin/beam] v$VERSION",
            LOCATION_EXPLORE_DROPDOWN_CALLOUT to "Explore",
            LOCATION_DAEMON_STATUS_CONNECTED to "Connected to daemon.",
            LOCATION_DAEMON_STATUS_DISCONNECTED to "Couldn't connect to daemon.",
            LOCATION_DAEMON_STATUS_MS_UNIT to "ms",
            LOCATION_SPACE_FAILURE_CARD_NOT_FOUND_ICON_ALT to "404 image",
            LOCATION_SPACE_FAILURE_CARD_NOT_FOUND_MESSAGE to "We couldn't find anything.",
            LOCATION_SPACE_FAILURE_CARD_EMPTY_SPACE_ICON_ALT to "Empty space icon",
            LOCATION_SPACE_FAILURE_CARD_EMPTY_SPACE_MESSAGE to "This space seems to be empty.",
            LOCATION_FOOTER_MESSAGE_FIRST_PART to "Running",
            LOCATION_FOOTER_MESSAGE_SECOND_PART to "Beam",
            LOCATION_FOOTER_SOURCE_CODE_LINK_MESSAGE to "Source code on GitHub",
            LOCATION_FOOTER_SOURCE_CODE_LINK_URL to "https://github.com/d1snin/beam",
            LOCATION_FOOTER_LANGUAGE_SWITCHER_MESSAGE to "Switch language...",
            LOCATION_SPACE_LISTING_MESSAGE to "Explore the spaces on this instance:",
            LOCATION_SPACE_LISTING_FETCH_MORE_BUTTON to "Load more"
        )
    )
}