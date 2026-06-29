package com.example.piringku.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.piringku.data.local.entity.FoodEntity

@Dao
interface FoodDao {

    @Query("SELECT * FROM foods")
    suspend fun getAll(): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE name LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): FoodEntity?

    @Query("SELECT COUNT(*) FROM foods")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foods: List<FoodEntity>)
}