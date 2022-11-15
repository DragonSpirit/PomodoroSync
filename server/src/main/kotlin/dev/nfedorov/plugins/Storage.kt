package dev.nfedorov.plugins

import dev.nfedorov.features.storage.DataStorage
import dev.nfedorov.features.storage.DatabaseStorage
import io.ktor.server.application.*

fun Application.configureStorage(): DataStorage {
    val username = environment.config.propertyOrNull("database.login")!!.getString()
    val password = environment.config.propertyOrNull("database.password")!!.getString()
    return DatabaseStorage(username, password)
}