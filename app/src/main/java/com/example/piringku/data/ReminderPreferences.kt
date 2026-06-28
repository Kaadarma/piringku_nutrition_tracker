package com.example.piringku.data

import android.content.Context
import com.example.piringku.model.MealType

data class ReminderConfig(
    val enabled: Boolean = true,
    val hour: Int = 7,
    val minute: Int = 0,
)

class ReminderPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)

    fun getReminder(mealType: MealType): ReminderConfig {
        val key = mealType.name.lowercase()
        val (defaultHour, defaultMinute) = defaultTime(mealType)
        return ReminderConfig(
            enabled = prefs.getBoolean("${key}_enabled", true),
            hour = prefs.getInt("${key}_hour", defaultHour),
            minute = prefs.getInt("${key}_minute", defaultMinute),
        )
    }

    fun setReminder(mealType: MealType, config: ReminderConfig) {
        val key = mealType.name.lowercase()
        prefs.edit()
            .putBoolean("${key}_enabled", config.enabled)
            .putInt("${key}_hour", config.hour)
            .putInt("${key}_minute", config.minute)
            .apply()
    }

    val allEnabled: Boolean
        get() = MealType.entries.all { getReminder(it).enabled }

    fun setAllEnabled(enabled: Boolean) {
        MealType.entries.forEach { setReminder(it, getReminder(it).copy(enabled = enabled)) }
    }

    companion object {
        @Volatile
        private var INSTANCE: ReminderPreferences? = null

        fun getInstance(context: Context): ReminderPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ReminderPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }

        private fun defaultTime(mealType: MealType): Pair<Int, Int> = when (mealType) {
            MealType.BREAKFAST -> 7 to 0
            MealType.LUNCH -> 12 to 0
            MealType.DINNER -> 18 to 0
            MealType.SNACK -> 19 to 0
        }
    }
}
