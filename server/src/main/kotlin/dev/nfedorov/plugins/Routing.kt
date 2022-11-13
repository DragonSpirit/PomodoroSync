package dev.nfedorov.plugins

import dev.nfedorov.features.login.LoginModel
import dev.nfedorov.features.login.LoginResponseModel
import dev.nfedorov.features.register.RegisterResponseModel
import dev.nfedorov.features.storage.DataStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

abstract class BaseMessage {
    val uuid = UUID.randomUUID().toString()
}

data class Message(val userKey: String) : BaseMessage()

fun Application.configureRouting(storage: DataStorage) {

    routing {
        post("/register") {
            val userToken = UUID.randomUUID().toString()
            val deviceKey = UUID.randomUUID().toString()
            val newUserId = storage.addUser(userToken)
            storage.addDevice(newUserId, deviceKey)

            call.respond(RegisterResponseModel(userKey = userToken, deviceKey = deviceKey))
        }

        post("/login") {
            val payload = call.receive<LoginModel>()
            val user = storage.getUserByKey(payload.userKey)

            if (user == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                val deviceToken = UUID.randomUUID().toString()
                storage.addDevice(user.id, deviceToken)
                call.respond(LoginResponseModel(token = deviceToken))
            }
        }

        get("/users") {
            call.respond(storage.getUsers())
        }

        get("/devices") {
            call.respond(storage.getRegisteredDevicesByUserKey(null))
        }
    }
}
