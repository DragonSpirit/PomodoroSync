package dev.nfedorov

import dev.nfedorov.plugins.*
import io.github.crackthecodeabhi.kreds.connection.Endpoint
import io.github.crackthecodeabhi.kreds.connection.KredsClient
import io.github.crackthecodeabhi.kreds.connection.newClient
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.collections.LinkedHashSet

lateinit var redisClient: KredsClient
val json = Json { encodeDefaults = true }
val connections: MutableSet<Connection> = Collections.synchronizedSet(LinkedHashSet())

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        val storage = configureStorage()
        configureSerialization()
        configureRouting(storage)
        configureSockets(storage)
        configureRabbitMq()
        newClient(Endpoint.from("localhost:6379")).use { client ->
            redisClient = client
        }
    }.start(wait = true)
}
