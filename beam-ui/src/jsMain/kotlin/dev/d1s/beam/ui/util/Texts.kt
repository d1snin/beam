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

import dev.d1s.beam.commons.VERSION

object Texts {

    object Heading {

        object Icon {

            const val ALT = "Beam space icon"
        }

        object SpaceInfo {

            const val DEFAULT_TITLE = "Beam"
        }

        object ExploreDropdown {

            const val CALLOUT = "Explore"
        }

        object DaemonStatus {

            const val CONNECTED = "Connected to daemon."

            const val DISCONNECTED = "Couldn't connect to daemon."

            const val MS_UNIT = "ms"
        }
    }

    object Body {

        object DisconnectedDaemonStatusReporter {

            const val ALERT_FIRST_LINE = "Beam is unavailable."
            const val ALERT_SECOND_LINE = "Check back later."
        }

        object SpaceFailureCard {

            object NotFoundMode {

                const val ICON_ALT = "404 image"

                const val TEXT = "We couldn't find anything."
            }

            object EmptySpaceMode {

                const val ICON_ALT = "Empty space icon"

                const val TEXT = "This space seems to be empty."
            }
        }
    }

    object Footer {

        const val TEXT_PART_1 = "Running"
        const val TEXT_PART_2 = "Beam"
        const val TEXT_PART_3 = "v$VERSION"

        const val SOURCE_CODE_REFERENCE = "Source code on GitHub"
        const val SOURCE_CODE_LINK = "https://github.com/d1snin/beam"
    }

    object SpaceListing {

        const val CALLOUT = "Explore the spaces on this instance:"

        const val FETCH_MORE_BUTTON = "Load more"
    }
}