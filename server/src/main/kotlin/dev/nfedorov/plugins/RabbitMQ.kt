package dev.nfedorov.plugins

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.nfedorov.connections
import dev.nfedorov.features.timer.TimerStatus
import dev.nfedorov.features.timer.TimerStatusResponse
import dev.nfedorov.json
import dev.nfedorov.redisClient
import io.ktor.server.application.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import pl.jutupe.ktor_rabbitmq.RabbitMQ
import pl.jutupe.ktor_rabbitmq.consume
import pl.jutupe.ktor_rabbitmq.rabbitConsumer

fun Application.configureRabbitMq() {
    install(RabbitMQ) {
        uri = "amqp://guest:guest@localhost:5672"
        connectionName = "Connection name"

        enableLogging()

        //serialize and deserialize functions are required
        serialize { jacksonObjectMapper().writeValueAsBytes(it) }
        deserialize { bytes, type -> jacksonObjectMapper().readValue(bytes, type.javaObjectType) }

        initialize {
            val args = buildMap { put("x-delayed-type", "direct") }
            exchangeDeclare(/* exchange = */ "exchange", /* type = */ "x-delayed-message", /* durable = */ true, false, args)
            queueDeclare(
                /* queue = */ "queue",
                /* durable = */true,
                /* exclusive = */false,
                /* autoDelete = */false,
                /* arguments = */emptyMap()
            )
            queueBind(/* queue = */ "queue", /* exchange = */ "exchange", /* routingKey = */ "routingKey")
        }
    }
    rabbitConsumer {
        consume<Message>("queue") { body ->
            val uuid = body.uuid
            launch {
                val isValid = redisClient.getDel(uuid).toBoolean()
                if (isValid) {
                    connections.filter { it.userId == body.userKey }.forEach { it.session.send(json.encodeToString(
                        TimerStatusResponse(uuid, TimerStatus.END)
                    )) }
                    println("Ring the bells")
                } else {
                    println("Invalid, skip")
                }
            }
        }
    }
}