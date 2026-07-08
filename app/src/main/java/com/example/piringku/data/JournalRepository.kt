package com.example.piringku.data

import android.content.Context
import com.example.piringku.data.local.AppDatabase
import com.example.piringku.data.local.entity.JournalEntryEntity
import com.example.piringku.model.DailyNutrition
import com.example.piringku.model.JournalEntry
import com.example.piringku.model.MealType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class JournalRepository private constructor(context: Context) {

    private val journalDao = AppDatabase.getInstance(context).journalDao()

    fun getEntriesByDate(date: LocalDate, userId: Long): Flow<List<JournalEntry>> {
        val start = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val end = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        return journalDao.getEntriesByDateRange(userId, start, end).map { entities ->
            entities.map { it.toJournalEntry() }
        }
    }

    fun getDailyNutrition(date: LocalDate, userId: Long): Flow<DailyNutrition> {
        val start = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val end = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        return journalDao.getEntriesByDateRange(userId, start, end).map { entities ->
            DailyNutrition(
                calories = entities.sumOf { it.calories.toDouble() }.toFloat(),
                protein = entities.sumOf { it.protein.toDouble() }.toFloat(),
                fat = entities.sumOf { it.fat.toDouble() }.toFloat(),
                carbs = entities.sumOf { it.carbs.toDouble() }.toFloat(),
            )
        }
    }

    /**
     * Ambil semua entry di antara [startDate] (inklusif) dan [endDateExclusive] (eksklusif).
     * Dipakai StatsScreen untuk hitung rata-rata mingguan/bulanan, rasio makro, dan makanan
     * yang paling sering dimakan pada periode tersebut.
     */
    fun getEntriesInRange(startDate: LocalDate, endDateExclusive: LocalDate, userId: Long): Flow<List<JournalEntry>> {
        val start = startDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val end = endDateExclusive.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        return journalDao.getEntriesByDateRange(userId, start, end).map { entities ->
            entities.map { it.toJournalEntry() }
        }
    }

    suspend fun addEntry(entry: JournalEntry): Long {
        return journalDao.insertEntry(entry.toEntity())
    }

    suspend fun updateEntry(entry: JournalEntry) {
        journalDao.updateEntry(entry.toEntity())
    }

    suspend fun deleteEntry(entry: JournalEntry) {
        journalDao.deleteEntry(entry.toEntity())
    }

    suspend fun deleteEntryById(id: Long, userId: Long) {
        journalDao.deleteEntryById(id, userId)
    }

    suspend fun getEntryById(id: Long, userId: Long): JournalEntry? {
        return journalDao.getEntryById(id, userId)?.toJournalEntry()
    }

    fun createEntryFromFood(
        userId: Long,
        foodId: Int,
        foodName: String,
        portion: Float,
        calories: Float,
        protein: Float,
        fat: Float,
        carbs: Float,
        mealType: MealType,
        imageUrl: String,
        timestamp: Instant = Instant.now(),
    ): JournalEntry {
        return JournalEntry(
            userId = userId,
            foodId = foodId,
            foodName = foodName,
            portion = portion,
            calories = calories * portion,
            protein = protein * portion,
            fat = fat * portion,
            carbs = carbs * portion,
            mealType = mealType,
            timestamp = timestamp,
            imageUrl = imageUrl,
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: JournalRepository? = null

        fun getInstance(context: Context): JournalRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: JournalRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}

private fun JournalEntryEntity.toJournalEntry(): JournalEntry {
    return JournalEntry(
        id = id,
        userId = userId,
        foodId = foodId,
        foodName = foodName,
        portion = portion,
        calories = calories,
        protein = protein,
        fat = fat,
        carbs = carbs,
        mealType = MealType.fromString(mealType),
        timestamp = Instant.ofEpochMilli(timestamp),
        imageUrl = imageUrl,
    )
}

private fun JournalEntry.toEntity(): JournalEntryEntity {
    return JournalEntryEntity(
        id = id,
        userId = userId,
        foodId = foodId,
        foodName = foodName,
        portion = portion,
        calories = calories,
        protein = protein,
        fat = fat,
        carbs = carbs,
        mealType = mealType.name,
        timestamp = timestamp.toEpochMilli(),
        imageUrl = imageUrl,
    )
}