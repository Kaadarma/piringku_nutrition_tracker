package com.example.piringku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
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
