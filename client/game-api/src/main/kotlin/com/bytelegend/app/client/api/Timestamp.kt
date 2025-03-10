/*
 * Copyright 2021 ByteLegend Technologies and the original author or authors.
 * 
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      https://github.com/ByteLegend/ByteLegend/blob/master/LICENSE
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bytelegend.app.client.api

import kotlin.js.Date

class Timestamp(
    private val epochTimeMs: Long
) {
    constructor() : this(Date().getTime().toLong())

    operator fun minus(other: Timestamp): Long = epochTimeMs - other.epochTimeMs
    operator fun compareTo(other: Timestamp) = epochTimeMs.compareTo(other.epochTimeMs)

    fun elapsedTimeMs() = Date().getTime().toLong() - this.epochTimeMs

    companion object {
        fun now() = Timestamp()
    }
}
