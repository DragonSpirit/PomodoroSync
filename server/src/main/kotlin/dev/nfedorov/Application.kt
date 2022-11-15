package dev.nfedorov

import com.typesafe.config.ConfigFactory
import dev.nfedorov.plugins.*
import io.github.crackthecodeabhi.kreds.connection.KredsClient
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.util.*

lateinit var redisClient: KredsClient
val json = Json { encodeDefaults = true }
val connections: MutableSet<Connection> = Collections.synchronizedSet(LinkedHashSet())
fun main() {
    embeddedServer(Netty, environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
        config = HoconApplicationConfig(ConfigFactory.load())

        module {
            val storage = configureStorage()
            configureSerialization()
            configureRouting(storage)
            configureSockets(storage)
            configureRabbitMq()
            configureRedis()
        }

        connector {
            port = 8080
            host = "0.0.0.0"
        }
    }).start(wait = true)
}
