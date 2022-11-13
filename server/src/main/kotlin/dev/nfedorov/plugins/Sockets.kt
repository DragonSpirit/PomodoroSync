package dev.nfedorov.plugins

import com.rabbitmq.client.AMQP
import dev.nfedorov.connections
import dev.nfedorov.features.storage.DataStorage
import dev.nfedorov.features.timer.*
import dev.nfedorov.json
import dev.nfedorov.redisClient
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pl.jutupe.ktor_rabbitmq.publish
import java.time.Duration

class Connection(val session: DefaultWebSocketSession, val userId: String)

suspend fun WebSocketSession.closeWithAuthError() =
    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Wrong auth header"))

suspend fun WebSocketSession.closeWithDeviceError() =
    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No such device"))

suspend fun WebSocketSession.closeWithUserError() =
    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No such user"))

fun Application.configureSockets(storage: DataStorage) {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/ws") {
            val deviceKey: String = call.request.headers["deviceKey"] ?: return@webSocket closeWithAuthError()
            val userId = storage.getUserByKey(deviceKey)?.key ?: return@webSocket closeWithUserError()

            storage.getDeviceByKey(deviceKey) ?: return@webSocket closeWithDeviceError()

            val connection = Connection(this, userId)
            connections += connection
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val action = Json.decodeFromString<Action>(frame.readText())
                        when (action.type) {
                            PayloadActions.NEW_TIMER -> {
                                val payload = Json.decodeFromString<NewTimerRequest>(action.payload)
                                val headers = buildMap { put("x-delay", payload.duration) }
                                val props = AMQP.BasicProperties.Builder().headers(headers)
                                val message = Message(userId)
                                call.publish("exchange", "routingKey", props.build(), message)
                                redisClient.set(message.uuid, "true")
                                send(json.encodeToString(TimerStatusResponse(message.uuid, TimerStatus.STARTED)))
                            }
                            PayloadActions.CANCEL_TIMER -> {
                                val payload = Json.decodeFromString<CancelTimerRequest>(action.payload)
                                redisClient.set(payload.uuid, "false")
                                send(json.encodeToString(TimerStatusResponse(payload.uuid, TimerStatus.CANCELLED)))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                connections -= connection
            }
        }
    }
}
