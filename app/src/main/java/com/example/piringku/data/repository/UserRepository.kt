package com.example.piringku.data.repository

import android.content.Context
import com.example.piringku.data.local.AppDatabase
import com.example.piringku.data.local.entity.UserEntity
import com.example.piringku.util.ProfilePictureManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class UserProfile(
    val id: Long = 0,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val height: Int = 170,
    val weight: Float = 65f,
    val age: Int = 25,
    val gender: String = "Pria",
    val activityLevel: String = "cukup_aktif",
    val targetWeight: Float = 68f,
    val goalCalories: Float = 2000f,
    val goalProtein: Float = 150f,
    val goalFat: Float = 65f,
    val goalCarbs: Float = 250f,
)

class UserRepository(context: Context) {

    private val userDao = AppDatabase.getInstance(context).userDao()

    fun getUserProfile(userId: Long): Flow<UserProfile> = userDao.getUser(userId).map { entity ->
        entity?.let { it.toProfile() } ?: UserProfile()
    }

    suspend fun getUserSnapshot(userId: Long): UserProfile {
        return userDao.getUserOnce(userId)?.toProfile() ?: UserProfile()
    }

    suspend fun register(name: String, email: String, password: String): Long {
        return userDao.insertUser(UserEntity(name = name, email = email, password = password))
    }

    suspend fun login(email: String, password: String): UserEntity? {
        val user = userDao.getUserByEmail(email)
        return if (user != null && user.password == password) user else null
    }

    suspend fun ensureUser(email: String, name: String, password: String, context: Context) {
        val existing = userDao.getUserByEmail(email)
        if (existing == null) {
            val newId = userDao.insertUser(UserEntity(name = name, email = email, password = password))
            ProfilePictureManager.delete(context, newId)
        }
    }

    suspend fun hasUser(userId: Long): Boolean {
        return userDao.getUserOnce(userId) != null
    }

    suspend fun saveUser(
        userId: Long,
        name: String = "",
        email: String = "",
        password: String = "",
        height: Int = 170,
        weight: Float = 65f,
        age: Int = 25,
        gender: String = "Pria",
        activityLevel: String = "cukup_aktif",
        targetWeight: Float = 68f,
        goalCalories: Float = 2000f,
        goalProtein: Float = 150f,
        goalFat: Float = 65f,
        goalCarbs: Float = 250f,
    ) {
        val existing = userDao.getUserOnce(userId)
        userDao.insertUser(
            (existing ?: UserEntity()).copy(
                id = userId,
                name = name,
                email = email,
                password = password.ifEmpty { existing?.password ?: "" },
                height = height,
                weight = weight,
                age = age,
                gender = gender,
                activityLevel = activityLevel,
                targetWeight = targetWeight,
                goalCalories = goalCalories,
                goalProtein = goalProtein,
                goalFat = goalFat,
                goalCarbs = goalCarbs,
            ),
        )
    }

    suspend fun clearUser(userId: Long) {
        userDao.deleteUser(userId)
    }

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(context: Context): UserRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}

private fun UserEntity.toProfile(): UserProfile {
    return UserProfile(
        id = id,
        name = name,
        email = email,
        height = height,
        weight = weight,
        age = age,
        gender = gender,
        activityLevel = activityLevel,
        targetWeight = targetWeight,
        goalCalories = goalCalories,
        goalProtein = goalProtein,
        goalFat = goalFat,
        goalCarbs = goalCarbs,
    )
}
