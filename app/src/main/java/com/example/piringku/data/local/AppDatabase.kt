package com.example.piringku.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.piringku.data.local.dao.JournalDao
import com.example.piringku.data.local.dao.UserDao
import com.example.piringku.data.local.entity.JournalEntryEntity
import com.example.piringku.data.local.entity.UserEntity
import com.example.piringku.data.local.dao.FoodDao
import com.example.piringku.data.local.entity.FoodEntity

@Database(
    entities = [UserEntity::class, JournalEntryEntity::class, FoodEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun journalDao(): JournalDao
    abstract fun foodDao(): FoodDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "piringku.db",
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
