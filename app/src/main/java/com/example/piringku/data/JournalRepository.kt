package com.example.piringku.data

import android.content.Context
import com.example.piringku.model.DailyNutrition
import com.example.piringku.model.JournalEntry
import com.example.piringku.model.MealType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.concurrent.atomic.AtomicLong

class JournalRepository private constructor(context: Context) {
    private val entries = MutableStateFlow<List<JournalEntry>>(emptyList())
    private val nextId = AtomicLong(1)

    fun getEntriesByDate(date: LocalDate): Flow<List<JournalEntry>> {
        val start = date.atStartOfDay(ZoneOffset.UTC).toInstant()
        val end = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()
        return entries.map { list ->
            list.filter { it.timestamp in start..end }.sortedBy { it.mealType.order }
        }
    }

    fun getDailyNutrition(date: LocalDate): Flow<DailyNutrition> {
        val start = date.atStartOfDay(ZoneOffset.UTC).toInstant()
        val end = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()
        return entries.map { list ->
            val dayEntries = list.filter { it.timestamp in start..end }
            DailyNutrition(
                calories = dayEntries.sumOf { it.calories.toDouble() }.toFloat(),
                protein = dayEntries.sumOf { it.protein.toDouble() }.toFloat(),
                fat = dayEntries.sumOf { it.fat.toDouble() }.toFloat(),
                carbs = dayEntries.sumOf { it.carbs.toDouble() }.toFloat(),
            )
        }
    }

    suspend fun addEntry(entry: JournalEntry): Long {
        val id = nextId.getAndIncrement()
        val newEntry = entry.copy(id = id)
        entries.value = entries.value + newEntry
        return id
    }

    suspend fun updateEntry(entry: JournalEntry) {
        entries.value = entries.value.map { if (it.id == entry.id) entry else it }
    }

    suspend fun deleteEntry(entry: JournalEntry) {
        entries.value = entries.value.filter { it.id != entry.id }
    }

    suspend fun deleteEntryById(id: Long) {
        entries.value = entries.value.filter { it.id != id }
    }

    suspend fun getEntryById(id: Long): JournalEntry? {
        return entries.value.find { it.id == id }
    }

    fun createEntryFromFood(
        foodId: Int,
        foodName: String,
        portion: Float,
        calories: Float,
        protein: Float,
        fat: Float,
        carbs: Float,
        mealType: MealType,
        imageUrl: String,
    ): JournalEntry {
        return JournalEntry(
            foodId = foodId,
            foodName = foodName,
            portion = portion,
            calories = calories * portion,
            protein = protein * portion,
            fat = fat * portion,
            carbs = carbs * portion,
            mealType = mealType,
            timestamp = Instant.now(),
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
