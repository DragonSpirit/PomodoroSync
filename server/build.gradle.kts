val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val ktor_rabbitmq_version: String by project
val rabbitmq_version: String by project
val jackson_version: String by project
val kreds_version: String by project

plugins {
    application
    kotlin("jvm") version "1.7.20"
    id("io.ktor.plugin") version "2.1.2"
                id("org.jetbrains.kotlin.plugin.serialization") version "1.7.20"
}

group = "dev.nfedorov"
version = "0.0.1"
application {
    mainClass.set("dev.nfedorov.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-websockets-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("com.github.JUtupe:ktor-rabbitmq:$ktor_rabbitmq_version")
    implementation("com.rabbitmq:amqp-client:$rabbitmq_version")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson_version")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jackson_version")
    implementation("io.github.crackthecodeabhi:kreds:$kreds_version")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}