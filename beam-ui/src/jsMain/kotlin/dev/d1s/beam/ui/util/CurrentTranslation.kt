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

package dev.d1s.beam.ui.util

import dev.d1s.beam.commons.GlobalTranslation
import dev.d1s.beam.commons.TextLocation
import dev.d1s.beam.commons.TranslatedText
import dev.d1s.beam.commons.Translation

private var lateInitCurrentTranslation = GlobalTranslation.Default

val currentTranslation get() = lateInitCurrentTranslation

val Translation.iconAlt: TranslatedText get() = requiredTranslation(GlobalTranslation.LOCATION_ICON_ALT)
val Translation.spaceInfoDefaultTitle: TranslatedText get() = requiredTranslation(GlobalTranslation.LOCATION_SPACE_INFO_DEFAULT_TITLE)
val Translation.exploreDropdownCallout: TranslatedText get() = requiredTranslation(GlobalTranslation.LOCATION_EXPLORE_DROPDOWN_CALLOUT)
val Translation.daemonStatusConnected: TranslatedText get() = requiredTranslation(GlobalTranslation.LOCATION_DAEMON_STATUS_CONNECTED)
val Translation.daemonStatusDisconnected: TranslatedText get() = requiredTranslation(GlobalTranslation.LOCATION_DAEMON_STATUS_DISCONNECTED)
val Translation.daemonStatusMsUnit: TranslatedText get() = requiredTranslation(GlobalTranslation.LOCATION_DAEMON_STATUS_MS_UNIT)
val Translation.disconnectedDaemonStatusReporterFirstLine: TranslatedText
    get() = requiredTranslation(
        GlobalTranslation.LOCATION_DISCONNECTED_DAEMON_STATUS_REPORTER_FIRST_LINE
    )
val Translation.disconnectedDaemonStatusReporterSecondLine: TranslatedText
    get() = requiredTranslation(
        GlobalTranslation.LOCATION_DISCONNECTED_DAEMON_STATUS_REPORTER_SECOND_LINE
    )
val Translation.spaceFailureCardNotFoundIconAlt: TranslatedText get() = requiredTranslation(GlobalTranslation.LOCATION_SPACE_FAILURE_CARD_NOT_FOUND_ICON_ALT)
val Translation.spaceFailureCardNotFoundMessage: TranslatedText get() = requiredTranslation(GlobalTranslation.LOCATION_SPACE_FAILURE_CARD_NOT_FOUND_MESSAGE)
val Translation.spaceFailureCardEmptySpaceIconAlt: TranslatedText get() = requiredTranslation(GlobalTranslation.LOCATION_SPACE_FAILURE_CARD_EMPTY_SPACE_ICON_ALT)
val Translation.spaceFailureCardEmptySpaceMessage: TranslatedText get() = requiredTranslation(GlobalTranslation.LOCATION_SPACE_FAILURE_CARD_EMPTY_SPACE_MESSAGE)
val Translation.footerMessageFirstPart: TranslatedText get() = requiredTranslation(GlobalTranslation.LOCATION_FOOTER_MESSAGE_FIRST_PART)
val Translation.footerMessageSecondPart: TranslatedText get() = requiredTranslation(GlobalTranslation.LOCATION_FOOTER_MESSAGE_SECOND_PART)
val Translation.footerSourceCodeLinkMessage: TranslatedText get() = requiredTranslation(GlobalTranslation.LOCATION_FOOTER_SOURCE_CODE_LINK_MESSAGE)
val Translation.footerSourceCodeLinkUrl: TranslatedText get() = requiredTranslation(GlobalTranslation.LOCATION_FOOTER_SOURCE_CODE_LINK_URL)
val Translation.spaceListingMessage: TranslatedText get() = requiredTranslation(GlobalTranslation.LOCATION_SPACE_LISTING_MESSAGE)
val Translation.spaceListingFetchMoreButton: TranslatedText get() = requiredTranslation(GlobalTranslation.LOCATION_SPACE_LISTING_FETCH_MORE_BUTTON)

private fun Translation.requiredTranslation(location: TextLocation) =
    translations[location] ?: error("No translation for given location '$location'")