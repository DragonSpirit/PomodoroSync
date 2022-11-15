package dev.nfedorov.features.storage

import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

interface User : Entity<User> {
    companion object : Entity.Factory<User>()
    val id: Long
    var key: String
}

interface Device : Entity<Device> {
    companion object : Entity.Factory<Device>()
    val id: Long
    var userId: Long
    var deviceKey: String
    var pushToken: String?
}

object Users : Table<User>("users") {
    val id = long("id").primaryKey().bindTo(User::id)
    val key = varchar("key").bindTo(User::key)
}

object Devices : Table<Device>("devices") {
    val id = long("id").primaryKey().bindTo(Device::id)
    val userId = long("userId").bindTo(Device::userId)
    val deviceKey = varchar("deviceKey").bindTo(Device::deviceKey)
    val token = varchar("token").bindTo(Device::pushToken)
}

fun User.mapToUser(): UserDataModel = UserDataModel(id, key)
fun Device.mapToDevice(): DeviceDataModel = DeviceDataModel(id, userId, deviceKey, pushToken)

class DatabaseStorage(username: String, password: String) : DataStorage {

    private val database = Database.connect(
        url = "jdbc:postgresql://localhost:5432/pomodorosync",
        driver = "org.postgresql.Driver",
        user = username,
        password = password
    )

    override fun addUser(token: String): Long {
        val user = User {
            key = token
        }
        val isAdded = database.sequenceOf(Users).add(user) == 1
        return if (isAdded) {
            database.sequenceOf(Users).find { it.key eq token }?.id ?: -1L
        } else {
            -1L
        }
    }

    override fun addDevice(userId: Long, token: String): Boolean {
        val device = Device {
            this.userId = userId
            this.deviceKey = token
        }
        return database.sequenceOf(Devices).add(device) == 1
    }

    override fun getUserByKey(key: String): UserDataModel? {
        return database.sequenceOf(Users).find { it.key eq key }?.mapToUser()
    }

    override fun getDeviceByKey(key: String): DeviceDataModel? {
        return database.sequenceOf(Devices).find { it.deviceKey eq key }?.mapToDevice()
    }

    override fun getUsers(): List<UserDataModel> {
        return database.sequenceOf(Users).map { it.mapToUser() }
    }

    override fun getRegisteredDevicesByUserKey(key: String?): List<DeviceDataModel> {
        return if (key == null) {
            database.sequenceOf(Devices).map { it.mapToDevice() }
        } else {
            database.sequenceOf(Devices).filter { it.deviceKey eq key }.map { it.mapToDevice() }
        }
    }

}