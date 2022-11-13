package dev.nfedorov.plugins

import dev.nfedorov.features.storage.DataStorage
import dev.nfedorov.features.storage.InMemoryStorage

fun configureStorage(): DataStorage {
    return InMemoryStorage
}