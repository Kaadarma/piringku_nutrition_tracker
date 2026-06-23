# Piringku - Nutrition Tracker

Aplikasi mobile Android untuk tracking nutrisi makanan Indonesia menggunakan Jetpack Compose dan Material Design 3.

## 🎯 Fitur

- **Journal** - Catat asupan makanan harian
- **Stats** - Lihat statistik nutrisi (kalori, protein, lemak, karb)
- **Cari** - Cari makanan dari dataset 1300+ items
- **Profile** - Kelola profil dan preferensi

## 🛠️ Tech Stack

| Komponen | Versi |
|----------|-------|
| Kotlin | 2.2.10 |
| Gradle | 9.3.1 |
| AGP | 9.1.1 |
| Compose | BOM 2024.09.00 |
| compileSdk | 36 |
| minSdk | 21 |

**Dependencies:**
- Jetpack Compose (Material 3)
- Navigation Compose (2.9.8)
- Gson (2.11.0) - JSON parsing
- Coil (2.7.0) - Image loading

## 📁 Struktur Project

```
app/
├── src/main/
│   ├── kotlin/com/example/piringku/
│   │   ├── MainActivity.kt
│   │   ├── MainScreens.kt
│   │   ├── data/
│   │   │   ├── FoodRepository.kt
│   │   │   └── JournalRepository.kt
│   │   ├── model/
│   │   │   ├── DailyNutrition.kt
│   │   │   ├── FoodItem.kt
│   │   │   ├── JournalEntry.kt
│   │   │   └── MealType.kt
│   │   └── ui/
│   │       ├── journal/
│   │       │   ├── JournalEntryCard.kt
│   │       │   ├── JournalScreen.kt
│   │       │   ├── FoodDetailSheet.kt
│   │       │   └── UpdateDeleteSheet.kt
│   │       ├── search/
│   │       │   ├── SearchScreen.kt
│   │       │   ├── SearchResultCard.kt
│   │       │   └── SkeletonLoader.kt
│   │       ├── stats/
│   │       │   └── StatsScreen.kt
│   │       └── theme/
│   │           ├── Color.kt
│   │           ├── Theme.kt
│   │           └── Type.kt
│   ├── assets/
│   │   └── food_data.json (277KB - 1346 items)
│   └── AndroidManifest.xml
├── build.gradle.kts
└── gradle/libs.versions.toml
```

## 🎨 Design System

**Warna Utama:**
- Primary (Fresh Green): `#0F5238`
- Secondary (Vibrant Orange): `#9B4500`
- Tertiary (Red): `#713638`

**Typography:**
- Headline: Inter Bold
- Body: Inter Regular
- Label: JetBrains Mono

**Spacing:** 8px grid base, 20px padding, 16px gutter
**Radius:** 16px cards, 24px hero, 12px buttons

Lihat `Aset/DESIGN.md` untuk detail lengkap.

## 🚀 Quick Start

### Build
```bash
./gradlew assembleDebug          # Build APK
./gradlew lintDebug              # Lint check
```

### Install & Run
```bash
./gradlew installDebug                      # Install ke device
adb shell am start -n com.example.piringku/.MainActivity
```

### Testing
```bash
./gradlew testDebugUnitTest             # Unit tests
./gradlew connectedDebugAndroidTest     # Instrumented tests
```

### CLI Development (Tanpa Android Studio)
```bash
# Setup device
adb devices
emulator -avd <name>

# Build & deploy
./gradlew assembleDebug --no-daemon
./gradlew installDebug

# Debug
adb logcat | grep piringku
adb logcat -c  # clear logs
```

Lihat `AGENTS.md` untuk workflow lengkap CLI development.

## 📊 Data

**Food Dataset:**
- Source: Kaggle "Indonesian Food and Drink Nutrition Dataset"
- Format: JSON (`app/src/main/assets/food_data.json`)
- Items: 1346 makanan Indonesia
- Fields: id, name, calories, proteins, fat, carbs, image

**Loading:**
```kotlin
val foods = FoodRepository.searchFood("nasi")
```

## 🔧 Development

**Team:**
- Orang 1: Journal screen (JournalScreen, JournalEntryCard, FoodDetailSheet, UpdateDeleteSheet)
- Orang 2: Stats screen (StatsScreen)
- Orang 3: Search/Cari search screen (SearchScreen, SearchResultCard)
- Orang 4: Backend (Room DB planned)

**Conventions:**
- Kotlin style: Kotlin官方规范
- Compose: Material 3 指南
- Naming: 驼峰命名函数，帕斯卡命名可组合项
- Navigation: 底部导航，4个路由

**Recent Fixes (2026-06-23):**
- ✅ SkeletonLoader MaterialTheme import
- ✅ Theme.kt dark mode logic
- ✅ AndroidManifest INTERNET permission
- ✅ DateTimeFormatter pattern crash fix (JournalScreen.kt)
- ✅ P0: Callback state on IO thread → withContext(Main)
- ✅ P1: Shared ModalBottomSheet state → separate states
- ✅ P2: Gson exception handling in FoodRepository

Lihat `logs/REPAIR_SUMMARY.md` untuk detail.

## 📝 Notes

- 单一 Activity + NavHost 架构
- Compose 编译器通过 `kotlin-compose` 插件
- 已实现 Journal 相关界面（JournalScreen, JournalEntryCard, FoodDetailSheet, UpdateDeleteSheet）
- Room 数据库计划中（Orang 4）
- 所有设计令牌位于 `ui/theme/`

## 📄 License

MIT

---

**Last Updated:** 2026-06-23  
**Status:** Active Development
