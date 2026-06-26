package com.example.piringku.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null
        fun getInstance(context: Context): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    data class UserData(
        val isLoggedIn: Boolean = false,
        val name: String = "",
        val email: String = "",
        val height: Int = 170,
        val weight: Float = 65f,
        val age: Int = 25,
        val gender: String = "Laki-laki",
        val activityLevel: String = "Sedang",
    )

    data class Goals(
        val calories: Float = 2000f,
        val protein: Float = 150f,
        val fat: Float = 65f,
        val carbs: Float = 250f,
    )

    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val NAME = stringPreferencesKey("name")
    private val EMAIL = stringPreferencesKey("email")
    private val HEIGHT = intPreferencesKey("height")
    private val WEIGHT = floatPreferencesKey("weight")
    private val AGE = intPreferencesKey("age")
    private val GENDER = stringPreferencesKey("gender")
    private val ACTIVITY_LEVEL = stringPreferencesKey("activity_level")
    private val GOAL_CALORIES = floatPreferencesKey("goal_calories")
    private val GOAL_PROTEIN = floatPreferencesKey("goal_protein")
    private val GOAL_FAT = floatPreferencesKey("goal_fat")
    private val GOAL_CARBS = floatPreferencesKey("goal_carbs")

    val userData: Flow<UserData> = context.dataStore.data.map { prefs ->
        UserData(
            isLoggedIn = prefs[IS_LOGGED_IN] ?: false,
            name = prefs[NAME] ?: "",
            email = prefs[EMAIL] ?: "",
            height = prefs[HEIGHT] ?: 170,
            weight = prefs[WEIGHT] ?: 65f,
            age = prefs[AGE] ?: 25,
            gender = prefs[GENDER] ?: "Laki-laki",
            activityLevel = prefs[ACTIVITY_LEVEL] ?: "Sedang",
        )
    }

    val goals: Flow<Goals> = context.dataStore.data.map { prefs ->
        Goals(
            calories = prefs[GOAL_CALORIES] ?: 2000f,
            protein = prefs[GOAL_PROTEIN] ?: 150f,
            fat = prefs[GOAL_FAT] ?: 65f,
            carbs = prefs[GOAL_CARBS] ?: 250f,
        )
    }

    suspend fun login(name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = true
            prefs[NAME] = name
            prefs[EMAIL] = email
        }
    }

    suspend fun saveDataDiri(height: Int, weight: Float, age: Int, gender: String, activityLevel: String) {
        context.dataStore.edit { prefs ->
            prefs[HEIGHT] = height
            prefs[WEIGHT] = weight
            prefs[AGE] = age
            prefs[GENDER] = gender
            prefs[ACTIVITY_LEVEL] = activityLevel
        }
    }

    suspend fun saveGoals(calories: Float, protein: Float, fat: Float, carbs: Float) {
        context.dataStore.edit { prefs ->
            prefs[GOAL_CALORIES] = calories
            prefs[GOAL_PROTEIN] = protein
            prefs[GOAL_FAT] = fat
            prefs[GOAL_CARBS] = carbs
        }
    }

    suspend fun logout() {
        context.dataStore.edit { it.clear() }
    }
}
