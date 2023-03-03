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

package dev.mbo.eventstreaming.sender

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.UUID
import dev.mbo.eventstreaming.random.RandomStringGenerator

@Component
class TimedSender(
    private val kafkaSender: KafkaGenericSender,
    @Value("\${app.kafka.topics.sample.name}")
    private val topic: String,
    @Value("\${app.kafka.topics.sample.initialDelayMillis}")
    private val initialDelayMillis: Long,
    @Value("\${app.kafka.topics.sample.delayMillis}")
    private val delayMillis: Long,
    @Value("\${app.kafka.topics.sample.batchSizeMin}")
    private val batchSizeMin: Int,
    @Value("\${app.kafka.topics.sample.batchSizeMax}")
    private val batchSizeMax: Int,
) : CommandLineRunner,
    DisposableBean {

    private val log: Logger = LoggerFactory.getLogger(javaClass)
    private val rnd = SecureRandom.getInstanceStrong()
    private val realBatchSizeMax = batchSizeMax + 1
    private var running = true

    init {
        if(topic.isBlank()) {
            throw IllegalStateException("topic can't be blank")
        }
        if(initialDelayMillis < 0) {
            throw IllegalStateException("initialDelayMillis can't be negative")
        }
        if(delayMillis < 0) {
            throw IllegalStateException("delayMillis can't be negative")
        }
        if (batchSizeMin < 0) {
            throw IllegalStateException("batchSizeMin can't be negative")
        }
        if (batchSizeMin > batchSizeMax) {
            throw IllegalStateException("batchSizeMin can't be higher than batchSizeMax")
        }
    }

    override fun run(vararg args: String?) {
        log.info(
            "start sending messages in {}ms",
            initialDelayMillis
        )
        Thread.sleep(initialDelayMillis)
        var messageNumber = 0
        while (running) {
            val batchSize = rnd.nextInt(
                batchSizeMin,
                realBatchSizeMax
            )
            log.info(
                "sending batch of {} messages",
                batchSize
            )
            (0 until batchSize).forEach {
                val key = UUID.randomUUID()
                val payload = RandomStringGenerator.randomString(128)
                if(running) {
                    kafkaSender.send(
                        topic = topic,
                        key = key,
                        payload = payload,
                        headers = mapOf("m_number" to messageNumber.toString()),
                    )
                    log.info(
                        "sent message #{} with key {} and body {}",
                        messageNumber,
                        key,
                        payload
                    )
                    messageNumber++
                }
            }
            if(running) {
                Thread.sleep(delayMillis)
            }
        }
    }

    fun stop() {
        log.info("stopping to send further messages")
        running = false
    }

    override fun destroy() {
        stop()
    }

}