package com.example.piringku.data

import android.content.Context

data class DailyTargets(
    val calories: Float = 2000f,
    val protein: Float = 150f,
    val fat: Float = 65f,
    val carbs: Float = 250f,
)

class TargetPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("target_prefs", Context.MODE_PRIVATE)

    fun getTargets(): DailyTargets = DailyTargets(
        calories = prefs.getFloat(KEY_CALORIES, 2000f),
        protein = prefs.getFloat(KEY_PROTEIN, 150f),
        fat = prefs.getFloat(KEY_FAT, 65f),
        carbs = prefs.getFloat(KEY_CARBS, 250f),
    )

    fun saveTargets(targets: DailyTargets) {
        prefs.edit()
            .putFloat(KEY_CALORIES, targets.calories)
            .putFloat(KEY_PROTEIN, targets.protein)
            .putFloat(KEY_FAT, targets.fat)
            .putFloat(KEY_CARBS, targets.carbs)
            .apply()
    }

    companion object {
        private const val KEY_CALORIES = "target_calories"
        private const val KEY_PROTEIN = "target_protein"
        private const val KEY_FAT = "target_fat"
        private const val KEY_CARBS = "target_carbs"

        @Volatile
        private var INSTANCE: TargetPreferences? = null

        fun getInstance(context: Context): TargetPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TargetPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
