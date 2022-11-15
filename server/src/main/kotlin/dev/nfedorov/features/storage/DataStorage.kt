package dev.nfedorov.features.storage

import kotlinx.serialization.Serializable

@Serializable
data class UserDataModel(
    val id: Long,
    val key: String
)

@Serializable
data class DeviceDataModel(
    val id: Long,
    val userId: Long,
    val deviceKey: String,
    val pushToken: String?
)

interface DataStorage {
    fun addUser(token: String): Long
    fun addDevice(userId: Long, token: String): Boolean

    fun getUserByKey(key: String): UserDataModel?
    fun getDeviceByKey(key: String): DeviceDataModel?

    fun getUsers(): List<UserDataModel>
    fun getRegisteredDevicesByUserKey(key: String?): List<DeviceDataModel>
}