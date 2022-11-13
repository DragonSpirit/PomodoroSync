package dev.nfedorov.features.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginModel(val userKey: String)

@Serializable
data class LoginResponseModel(val token: String)