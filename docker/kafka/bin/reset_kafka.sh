#!/bin/bash
kafka-topics --bootstrap-server kafka:9092 --delete --topic '.*'