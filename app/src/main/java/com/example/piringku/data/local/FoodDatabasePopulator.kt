package com.example.piringku.data.local

import android.content.Context
import android.util.Log
import com.example.piringku.data.local.entity.FoodEntity
import com.example.piringku.model.FoodItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FoodDatabasePopulator {

    private const val TAG = "FoodDatabasePopulator"
    private const val ASSET_FILE_NAME = "food_data.json"

    suspend fun populateIfEmpty(context: Context, database: AppDatabase) {
        val foodDao = database.foodDao()
        if (foodDao.count() > 0) {
            return
        }

        val foodItems = readFoodItemsFromAssets(context)
        if (foodItems.isEmpty()) {
            Log.w(TAG, "Tidak ada data makanan yang berhasil dibaca dari $ASSET_FILE_NAME")
            return
        }

        val entities = foodItems.map { it.toEntity() }
        foodDao.insertAll(entities)
        Log.i(TAG, "Berhasil mengisi tabel foods dengan ${entities.size} item")
    }

    private fun readFoodItemsFromAssets(context: Context): List<FoodItem> {
        return try {
            val json = context.assets.open(ASSET_FILE_NAME)
                .bufferedReader()
                .use { it.readText() }

            if (json.isBlank()) {
                emptyList()
            } else {
                val type = object : TypeToken<List<FoodItem>>() {}.type
                Gson().fromJson(json, type)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal membaca/parse $ASSET_FILE_NAME", e)
            emptyList()
        }
    }

    private fun FoodItem.toEntity(): FoodEntity {
        return FoodEntity(
            id = id,
            name = name,
            calories = calories,
            proteins = proteins,
            fat = fat,
            carbs = carbs,
            image = image,
        )
    }
}