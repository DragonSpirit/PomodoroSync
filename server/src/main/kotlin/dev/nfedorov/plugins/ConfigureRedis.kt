package dev.nfedorov.plugins

import dev.nfedorov.redisClient
import io.github.crackthecodeabhi.kreds.connection.Endpoint
import io.github.crackthecodeabhi.kreds.connection.newClient

fun configureRedis() {
    newClient(Endpoint.from("localhost:6379")).use { client ->
        redisClient = client
    }
}