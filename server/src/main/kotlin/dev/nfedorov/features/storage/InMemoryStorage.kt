package dev.nfedorov.features.storage

import java.util.concurrent.atomic.AtomicLong

object InMemoryStorage : DataStorage {
    private var userId = AtomicLong(0)
    private var deviceId = AtomicLong(0)

    private val userList = mutableListOf<UserDataModel>()
    private val devicesList = mutableListOf<DeviceDataModel>()

    private fun getNewUserId(): Long {
        return userId.incrementAndGet()
    }

    private fun getNewDeviceId(): Long {
        return deviceId.incrementAndGet()
    }

    override fun addUser(token: String): Long {
        val user = UserDataModel(getNewUserId(), token)
        userList.add(user)
        return user.id
    }

    override fun addDevice(userId: Long, token: String): Boolean {
        devicesList.add(DeviceDataModel(
            id = getNewDeviceId(),
            userId = userId,
            deviceKey = token,
            pushToken = null
        ))
        return true
    }

    override fun getUserByKey(key: String): UserDataModel? {
        return userList.firstOrNull { it.key == key }
    }

    override fun getDeviceByKey(key: String): DeviceDataModel? {
        return devicesList.firstOrNull { it.deviceKey == key}
    }

    override fun getUsers(): List<UserDataModel> {
        return userList
    }

    override fun getRegisteredDevicesByUserKey(key: String?): List<DeviceDataModel> {
        return if (key == null) {
            devicesList
        } else {
            val userId = getUserByKey(key)?.id ?: -1
            devicesList.filter { it.userId == userId }
        }
    }
}