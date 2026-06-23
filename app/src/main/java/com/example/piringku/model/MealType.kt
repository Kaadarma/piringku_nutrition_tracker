package com.example.piringku.model

enum class MealType(val displayName: String, val order: Int) {
    BREAKFAST("Sarapan", 0),
    LUNCH("Makan Siang", 1),
    DINNER("Makan Malam", 2),
    SNACK("Camilan", 3);

    companion object {
        fun fromString(value: String): MealType = values().firstOrNull { it.name == value } ?: BREAKFAST
    }
}