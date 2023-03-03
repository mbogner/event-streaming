# event-streaming

This application show how to setup a single node Kafka instance with web interface named kafdrop reachable under
http://localhost:9000. The dev environment is configured using docker compose.

ATTENTION: You need to add an entry to your /etc/hosts to map kafka to 127.0.0.1.

## Structure

The sample consists of an application called `app` and a second one called `sender`.

### App

Shows how to create a configurable kafka listener and process batches of messages. All messages are treated as string
key value pairs and the application code itself can decide how to parse it. This way the developer has more power how to
handle certain cases.

### Sender

This was added to have an easy way to send messages to the topic. It supports an initial delay and a delay between
batches of messages. So before starting to send to `app.kafka.topics.sample.name` it
waits `app.kafka.topics.sample.initialDelayMillis` before the first message is sent. Then a batch
of `app.kafka.topics.sample.batchSizeMin` to `app.kafka.topics.sample.batchSizeMax` messages is sent. This is repeated
after `app.kafka.topics.sample.delayMillis`. Default is to wait for 3 seconds before first messages are sent and a batch
of 1-10 messages is sent every 2 seconds.