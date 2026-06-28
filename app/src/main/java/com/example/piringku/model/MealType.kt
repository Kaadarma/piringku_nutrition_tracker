package com.example.piringku.model

enum class MealType(val displayName: String, val order: Int, val notifId: Int, val notifTitle: String, val notifBody: String) {
    BREAKFAST("Sarapan", 0, 1001, "\u2600\uFE0F Waktunya Sarapan!", "Catat menu sarapanmu di Piringku."),
    LUNCH("Makan Siang", 1, 1002, "\uD83C\uDF24\uFE0F Waktunya Makan Siang!", "Jangan lupa catat makanan siangmu."),
    DINNER("Makan Malam", 2, 1003, "\uD83C\uDF19 Waktunya Makan Malam!", "Catat sebelum tidur, lihat progres kalorimu."),
    SNACK("Camilan", 3, 1004, "\uD83C\uDF1D Cek Progres Harian!", "Sudah catat semua makanan hari ini? Cek target kalorimu.");

    companion object {
        fun fromString(value: String): MealType = values().firstOrNull { it.name == value } ?: BREAKFAST
    }
}