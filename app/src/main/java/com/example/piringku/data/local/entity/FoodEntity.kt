package com.example.piringku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val calories: Float,
    val proteins: Float,
    val fat: Float,
    val carbs: Float,
    val image: String,
)