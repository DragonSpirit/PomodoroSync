package dev.nfedorov.features.register

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseModel(val userKey: String, val deviceKey: String)
