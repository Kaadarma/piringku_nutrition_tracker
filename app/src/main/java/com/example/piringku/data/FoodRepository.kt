package com.example.piringku.data

import android.content.Context
import com.example.piringku.model.FoodItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class FoodRepository private constructor(context: Context) {
    private var foodCache: List<FoodItem>? = null
    private val json: String by lazy {
        try {
            context.assets.open("food_data.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (e: IOException) {
            ""
        }
    }

    suspend fun loadFoods(): List<FoodItem> {
        foodCache?.let { return it }
        if (json.isBlank()) return emptyList()

        val type = object : TypeToken<List<FoodItem>>() {}.type
        val items: List<FoodItem> = Gson().fromJson(json, type)
        foodCache = items
        return items
    }

    fun searchFoods(query: String): List<FoodItem> {
        val cache = foodCache ?: return emptyList()
        if (query.isBlank()) return emptyList()
        return cache.filter { item ->
            item.name.contains(query, ignoreCase = true)
        }
    }

    fun getFoodById(id: Int): FoodItem? {
        return foodCache?.find { it.id == id }
    }

    companion object {
        @Volatile
        private var INSTANCE: FoodRepository? = null

        fun getInstance(context: Context): FoodRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FoodRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
