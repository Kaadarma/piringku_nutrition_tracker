package com.example.piringku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val foodId: Int,
    val foodName: String,
    val portion: Float,
    val calories: Float,
    val protein: Float,
    val fat: Float,
    val carbs: Float,
    val mealType: String,
    val timestamp: Long,
    val imageUrl: String = "",
)
