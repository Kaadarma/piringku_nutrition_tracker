package com.example.piringku.model

import java.time.Instant

data class JournalEntry(
    val id: Long = 0,
    val foodId: Int,
    val foodName: String,
    val portion: Float,
    val calories: Float,
    val protein: Float,
    val fat: Float,
    val carbs: Float,
    val mealType: MealType,
    val timestamp: Instant = Instant.now(),
    val imageUrl: String = "",
) {
    fun toDailyNutrition(): DailyNutrition {
        return DailyNutrition(
            calories = calories,
            protein = protein,
            fat = fat,
            carbs = carbs,
        )
    }

    fun withPortion(newPortion: Float): JournalEntry {
        val ratio = newPortion / this.portion
        return copy(
            portion = newPortion,
            calories = (calories * ratio),
            protein = (protein * ratio),
            fat = (fat * ratio),
            carbs = (carbs * ratio),
        )
    }
}
