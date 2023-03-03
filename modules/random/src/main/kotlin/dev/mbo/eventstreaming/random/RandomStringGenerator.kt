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
import kotlin.reflect.KFunction

object RandomStringGenerator {

    private val random = SecureRandom.getInstanceStrong()

    fun randomString(
        length: Int,
        lowercase: Boolean = true,
        uppercase: Boolean = true,
        numbers: Boolean = true
    ): String {
        if (length < 1) {
            throw IllegalArgumentException("length must be > 0")
        }
        val randomizers = mutableListOf<KFunction<Char>>()
        if (lowercase) {
            randomizers.add(RandomLetterGenerator::randomLowerCase)
        }
        if (uppercase) {
            randomizers.add(RandomLetterGenerator::randomUpperCase)
        }
        if (numbers) {
            randomizers.add(RandomLetterGenerator::randomNumber)
        }
        if (randomizers.size < 1) {
            throw IllegalArgumentException("use at least one randomizer")
        }

        val result = StringBuilder()
        for (i in 0 until length) {
            val randomizer = randomizers[random.nextInt(
                0,
                randomizers.size
            )]
            result.append(randomizer.call())
        }
        return result.toString()
    }

}