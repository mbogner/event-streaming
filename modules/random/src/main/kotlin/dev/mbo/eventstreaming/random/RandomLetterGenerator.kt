/*
 * Copyright (c) 2023.
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

package dev.mbo.eventstreaming.random

import java.security.SecureRandom

object RandomLetterGenerator {

    private val random = SecureRandom.getInstanceStrong()

    private const val UPPER_FIRST = 'A'
    private const val UPPER_LAST = 'Z'

    private const val LOWER_FIRST = 'a'
    private const val LOWER_LAST = 'z'

    private const val NUMBER_FIRST = '0'
    private const val NUMBER_LAST = '9'

    fun randomUpperCase(): Char {
        return random(
            UPPER_FIRST,
            UPPER_LAST
        )
    }

    fun randomLowerCase(): Char {
        return random(
            LOWER_FIRST,
            LOWER_LAST
        )
    }

    fun randomNumber(): Char {
        return random(
            NUMBER_FIRST,
            NUMBER_LAST
        )
    }

    private fun random(
        first: Char,
        last: Char
    ): Char {
        return random.nextInt(
            first.code,
            last.code + 1
        ).toChar()
    }
}