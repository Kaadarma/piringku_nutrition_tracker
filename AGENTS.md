# PIRINGKU - AGENTS.md

## Stack
- Kotlin 2.2.10, AGP 9.1.1, Gradle 9.3.1, compileSdk 36, minSdk 21
- Jetpack Compose (Material 3, Compose BOM 2024.09.00)
- Single module `:app` (package: `com.example.piringku`)
- Version catalog: `gradle/libs.versions.toml`

## Build Commands
```bash
./gradlew assembleDebug          # build
./gradlew installDebug           # install on device
./gradlew testDebugUnitTest      # unit tests
./gradlew connectedDebugAndroidTest  # instrumented tests
./gradlew lintDebug              # lint
```

## Architecture
- Single Activity (`MainActivity.kt`) + NavHost with bottom navigation (4 tabs: Journal, Stats, Cari, Profile)
- Navigation routes: `journal`, `stats`, `cari`, `profile`
- Compose Compiler is Kotlin 2.x built-in (via `kotlin-compose` plugin)

## Design System
Canonical source: `Aset/DESIGN.md`
- **Primary (Fresh Green)**: `#0F5238` — main brand, primary actions
- **Secondary (Vibrant Orange)**: `#9B4500` — energy metrics, warnings
- **Semantic Colors**: Blue for hydration, Red for "limit exceeded"/destructive
- **Typography**: Inter (body/headings) + JetBrains Mono (labels/units)
- **Spacing**: 8px base grid, `container-padding: 20px`, `gutter: 16px`
- **Shapes**: 16px radius cards, 24px radius hero widgets, 12px radius buttons

Theme already updated in `ui/theme/Color.kt`, `Theme.kt`, `Type.kt` to match DESIGN.md.

## Food Dataset
- Source: Kaggle "Indonesian Food and Drink Nutrition Dataset" (1346 items)
- Format: `app/src/main/assets/food_data.json` (277KB)
- Columns: `id`, `name`, `calories`, `proteins`, `fat`, `carbs`, `image`
- Conversion script: `scripts/csv_to_json.ps1`

## Key Files
| File | Purpose |
|---|---|
| `MainActivity.kt` | Single activity, NavHost setup |
| `MainScreens.kt` | Placeholder screens (Journal, Stats, Profile) |
| `data/FoodRepository.kt` | Singleton, loads JSON, search by name |
| `model/FoodItem.kt` | Data class for food items |
| `ui/search/SearchScreen.kt` | Search UI with tabs, skeleton loading |
| `ui/search/SearchResultCard.kt` | Food item card with AsyncImage |
| `ui/search/SkeletonLoader.kt` | Shimmer animation for loading state |
| `ui/journal/JournalScreen.kt` | Journal dashboard with macro ring & meal list |
| `ui/journal/JournalEntryCard.kt` | Journal entry row card |
| `ui/journal/FoodDetailSheet.kt` | Bottom sheet: food detail, portion, meal type |
| `ui/journal/UpdateDeleteSheet.kt` | Bottom sheet: update portion, delete entry |
| `ui/stats/StatsScreen.kt` | Stats page with charts & nutrient achievement |
| `data/JournalRepository.kt` | In-memory journal CRUD with date queries |
| `model/JournalEntry.kt` | Data class for journal entries |
| `model/DailyNutrition.kt` | Daily nutrition totals |
| `model/MealType.kt` | Enum: Sarapan, Makan Siang, Makan Malam, Camilan |
| `Aset/DESIGN.md` | Design system (colors, typography, spacing) |

## Dependencies
- **Gson** (`2.11.0`): Parse food JSON
- **Coil** (`2.7.0`): AsyncImage for food photos
- **Navigation Compose** (`2.9.8`): Bottom nav + screen routing


## CLI Development (No Android Studio)

### Setup
```bash
# Install Android SDK (or use existing)
# Ensure adb, emulator in PATH

# Device/Emulator
adb devices                           # list connected devices
emulator -list-avds                   # list emulators
emulator -avd <name>                  # launch emulator
```

### Build & Deploy
```bash
./gradlew assembleDebug --no-daemon   # build APK
./gradlew installDebug                # install on device
adb shell am start -n com.example.piringku/.MainActivity
```

### Testing & Debugging
```bash
./gradlew testDebugUnitTest           # unit tests
./gradlew connectedDebugAndroidTest   # instrumented tests
adb logcat                            # view logs
adb logcat | grep piringku            # filter logs
```

## Stitch Screens (5 screens from Project ID: 7150230184243051524)

| Screen | File | Route | Status |
|--------|------|-------|--------|
| Pencarian Makanan (Screen 4) | `ui/search/SearchScreen.kt` | cari | ✅ Done |
| Dashboard Jurnal Makanan | `ui/journal/JournalScreen.kt` | journal | ✅ Done |
| Detail & Input Porsi (Screen 3) | `ui/journal/FoodDetailSheet.kt` | journal (bottom sheet) | ✅ Done |
| Update & Delete Food Modal (Screens 1 & 5) | `ui/journal/UpdateDeleteSheet.kt` | journal (bottom sheet) | ✅ Done |
| Statistik Nutrisi & Progres (Screen 2) | `ui/stats/StatsScreen.kt` | stats | ✅ Done |

### Referensi Desain
- Semua HTML source ada di `C:\Users\Juana\AppData\Local\Temp\opencode\screen_*.html`
- Screenshot ada di `C:\Users\Juana\AppData\Local\Temp\opencode\screen_*.png`

## Recent Fixes (2026-06-23)

### P0 Critical Issues - RESOLVED ✅
1. **SkeletonLoader.kt:26** - Added missing `MaterialTheme` import
2. **Theme.kt** - Created `DarkColorScheme`, fixed dark theme logic (line 90)
3. **AndroidManifest.xml:5** - Added `INTERNET` permission for image loading

Details: See `logs/REPAIR_SUMMARY.md`

## Response
- setiap response simpan di file logs/nama_file.md gunakan format nama file logs yang deskriptif dalam format.md 
- gunakan bahasa yang singkat untuk menghemat token
- Lintasan CLI development tanpa Android Studio didokumentasikan di atas