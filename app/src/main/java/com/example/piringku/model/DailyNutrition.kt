package com.example.piringku.model

data class DailyNutrition(
    val calories: Float = 0f,
    val protein: Float = 0f,
    val fat: Float = 0f,
    val carbs: Float = 0f,
) {
    operator fun plus(other: DailyNutrition): DailyNutrition {
        return DailyNutrition(
            calories = this.calories + other.calories,
            protein = this.protein + other.protein,
            fat = this.fat + other.fat,
            carbs = this.carbs + other.carbs,
        )
    }

    fun scaled(portion: Float): DailyNutrition {
        return DailyNutrition(
            calories = this.calories * portion,
            protein = this.protein * portion,
            fat = this.fat * portion,
            carbs = this.carbs * portion,
        )
    }
}