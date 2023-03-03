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

package dev.mbo.eventstreaming.listener

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.listener.BatchAcknowledgingMessageListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class SampleListener : BatchAcknowledgingMessageListener<String, String> {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["#{'\${app.kafka.topics.sample.name}'}"],
        autoStartup = "\${app.kafka.config.auto-startup}"
    )
    override fun onMessage(
        records: MutableList<ConsumerRecord<String, String>>,
        acknowledgment: Acknowledgment
    ) {
        log.info(
            "received batch of {} messages",
            records.size
        )
        var batchIndex: Int = Int.MIN_VALUE
        try {
            // do not change order as the batch index is used for the nack in case of problems.
            records.forEachIndexed { i, record ->
                batchIndex = i
                processRecord(record)
            }
            acknowledgment.acknowledge()
        } catch (exc: Exception) {
            nack(
                batchIndex,
                acknowledgment,
                exc
            )
        }
    }

    private fun processRecord(record: ConsumerRecord<String, String>) {
        log.info(
            "received message #{} on topic {} with key {} and body {}",
            extractMNumberHeader(record),
            record.topic(),
            record.key(),
            record.value()
        )
    }

    private fun extractMNumberHeader(record: ConsumerRecord<String, String>): Int {
        return String(record.headers().headers("m_number").toList().first().value()).toInt()
    }

    private fun nack(
        index: Int,
        acknowledgment: Acknowledgment,
        exc: Exception
    ) {
        if (index != Int.MIN_VALUE) {
            log.error(
                "processing failed at index {}",
                index,
                exc
            )
            acknowledgment.nack(
                index,
                Duration.ofMillis(1000)
            )
        } else {
            throw IllegalStateException("index not updated shouldn't happen")
        }
    }

}