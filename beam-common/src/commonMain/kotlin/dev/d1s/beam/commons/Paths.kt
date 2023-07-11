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

public object Paths {

    public const val ID_PARAMETER: String = "id"

    public const val LIMIT_QUERY_PARAMETER: String = "limit"
    public const val OFFSET_QUERY_PARAMETER: String = "offset"

    public const val POST_SPACE: String = "/spaces"
    public const val POST_ROOT_SPACE: String = "/spaces/root"
    public const val GET_SPACE: String = "/spaces/{$ID_PARAMETER}"
    public const val GET_SPACES: String = "/spaces"
    public const val PUT_SPACE: String = "/spaces/{$ID_PARAMETER}"
    public const val PUT_ROOT_SPACE: String = "/spaces/root"
    public const val DELETE_SPACE: String = "/spaces/{$ID_PARAMETER}"

    public const val POST_BLOCK: String = "/blocks"
    public const val GET_BLOCKS: String = "/blocks"
    public const val PUT_BLOCK: String = "/blocks/{$ID_PARAMETER}"
    public const val DELETE_BLOCK: String = "/blocks/{$ID_PARAMETER}"
}