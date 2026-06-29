package com.example.piringku.data

import android.content.Context
import com.example.piringku.data.local.AppDatabase
import com.example.piringku.data.local.FoodDatabasePopulator
import com.example.piringku.data.local.entity.FoodEntity
import com.example.piringku.model.FoodItem

class FoodRepository private constructor(private val context: Context) {

    private val foodDao = AppDatabase.getInstance(context).foodDao()
    private var foodCache: List<FoodItem>? = null

    suspend fun loadFoods(): List<FoodItem> {
        foodCache?.let { return it }

        FoodDatabasePopulator.populateIfEmpty(context, AppDatabase.getInstance(context))

        val items = foodDao.getAll().map { it.toFoodItem() }
        foodCache = items
        return items
    }

    suspend fun searchFoods(query: String): List<FoodItem> {
        if (query.isBlank()) return emptyList()
        return foodDao.search(query).map { it.toFoodItem() }
    }

    suspend fun getFoodById(id: Int): FoodItem? {
        foodCache?.find { it.id == id }?.let { return it }
        return foodDao.getById(id)?.toFoodItem()
    }

    fun getRecommendations(count: Int = 8): List<FoodItem> {
        return foodCache?.take(count) ?: emptyList()
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

private fun FoodEntity.toFoodItem(): FoodItem {
    return FoodItem(
        id = id,
        name = name,
        calories = calories,
        proteins = proteins,
        fat = fat,
        carbs = carbs,
        image = image,
    )
}