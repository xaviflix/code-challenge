#!/usr/bin/env bash

./mvnw clean package -Dmaven.test.skip
docker stop database
docker stop event-queues
docker stop application
docker rm database
docker rm event-queues
docker rm application
docker rmi code-challenge
docker compose up -d

#java -jar ./target/CodeChallenge-0.0.1.jar
