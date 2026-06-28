package com.example.piringku.data

import android.content.Context
import com.example.piringku.model.FoodItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryManager(context: Context) {

    private val prefs = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getHistory(): List<FoodItem> {
        val json = prefs.getString("history", null) ?: return emptyList()
        val type = object : TypeToken<List<FoodItem>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun addToHistory(food: FoodItem) {
        val history = getHistory().toMutableList()
        history.removeAll { it.id == food.id }
        history.add(0, food)
        val trimmed = history.take(30)
        prefs.edit().putString("history", gson.toJson(trimmed)).apply()
    }

    fun clearHistory() {
        prefs.edit().remove("history").apply()
    }

    companion object {
        @Volatile
        private var INSTANCE: SearchHistoryManager? = null

        fun getInstance(context: Context): SearchHistoryManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SearchHistoryManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
