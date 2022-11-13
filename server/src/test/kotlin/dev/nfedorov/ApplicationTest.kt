package dev.nfedorov

import dev.nfedorov.features.storage.InMemoryStorage
import dev.nfedorov.plugins.configureRouting
import dev.nfedorov.plugins.configureSerialization
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureSerialization()
            configureRouting(InMemoryStorage)
        }
        client.post("/register").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}