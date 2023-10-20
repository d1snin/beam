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

public object Limits {

    public const val VIEW_TITLE_MAX_LENGTH: Int = 100
    public const val VIEW_DESCRIPTION_MAX_LENGTH: Int = 700

    public const val METADATA_MAX_LENGTH: Int = 100
    public const val METADATA_VALUE_MAX_LENGTH: Int = 500

    public const val SPACE_MAX_CAPACITY: Int = 300
    public const val BLOCK_MAX_CAPACITY: Int = 50
}