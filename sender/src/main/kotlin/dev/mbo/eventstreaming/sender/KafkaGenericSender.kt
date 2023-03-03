package dev.mbo.eventstreaming.sender

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture

@Component
class KafkaGenericSender(
    private val objectMapper: ObjectMapper,
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Async
    fun <K, V> send(
        topic: String,
        key: K,
        payload: V,
        headers: Map<String, String> = emptyMap()
    ): CompletableFuture<SendResult<String, String>> {
        val keyJson = objectMapper.writeValueAsString(key)
        val payloadJson = objectMapper.writeValueAsString(payload)
        log.debug(
            "sending key={}, payload={} to topic {}",
            keyJson,
            payloadJson,
            topic
        )
        val headerRecords = mapToHeader(headers)
        val record = createRecord(
            topic = topic,
            key = keyJson,
            value = payloadJson,
            headers = headerRecords,
        )

        return kafkaTemplate.send(record)
    }

    fun createRecord(
        topic: String,
        key: String,
        value: String,
        headers: List<Header>
    ): ProducerRecord<String, String> {
        return ProducerRecord(
            topic,
            null,
            null,
            key,
            value,
            headers
        )
    }

    fun mapToHeader(headers: Map<String, String>): List<Header> {
        return headers.map { (key, value) ->
            RecordHeader(
                key,
                value.toByteArray(StandardCharsets.UTF_8)
            )
        }.toList()
    }

}
