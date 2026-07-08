# Piringku - Nutrition Tracker

Aplikasi mobile Android untuk tracking nutrisi makanan Indonesia menggunakan Jetpack Compose, Material Design 3, dan Room Database.

## Fitur

- **Auth** - Register, login, data diri, multi-user support
- **Journal** - Catat asupan makanan harian dengan target nutrisi per hari
- **Stats** - Statistik nutrisi (kalori, protein, lemak, karbo) & progress goals
- **Cari** - Cari makanan dari dataset 1346 item (RIwayat pencarian)
- **Profile** - Kelola profil, foto, target nutrisi, reminder makan
- **Reminder** - Pengingat makan pagi/siang/malam via notifikasi

## Tech Stack

| Komponen | Versi |
|----------|-------|
| Kotlin | 2.2.10 |
| Gradle | 9.3.1 |
| AGP | 9.1.1 |
| Compose BOM | 2024.09.00 |
| Room | 2.6.1 |
| compileSdk | 36 |
| minSdk | 21 |

**Dependencies:**
- Jetpack Compose (Material 3), Navigation Compose 2.9.8
- Room 2.6.1 (database), Gson 2.11.0 (JSON parsing)
- Coil 2.7.0 (image loading)

## Struktur Project

```
app/src/main/kotlin/com/example/piringku/
├── MainActivity.kt              # Single Activity + NavHost
├── MainScreens.kt               # Bottom nav (4 tabs)
├── data/
│   ├── FoodRepository.kt        # JSON food dataset loader
│   ├── JournalRepository.kt     # Journal CRUD (in-memory)
│   ├── TargetPreferences.kt     # Target nutrisi per user
│   ├── UserPreferences.kt       # Session user ID
│   ├── SearchHistoryManager.kt  # Search history (SharedPrefs)
│   ├── ReminderPreferences.kt   # Reminder settings
│   ├── repository/
│   │   └── UserRepository.kt    # User CRUD via Room
│   └── local/
│       ├── AppDatabase.kt       # Room DB (version 4)
│       ├── dao/ (FoodDao, JournalDao, UserDao)
│       ├── entity/ (FoodEntity, JournalEntryEntity, UserEntity)
│       └── FoodDatabasePopulator.kt
├── model/
│   ├── DailyNutrition.kt
│   ├── FoodItem.kt
│   ├── JournalEntry.kt
│   └── MealType.kt
├── ui/
│   ├── auth/ (LoginScreen, RegisterScreen, DataDiriScreen)
│   ├── journal/ (JournalScreen, JournalEntryCard, FoodDetailSheet, UpdateDeleteSheet)
│   ├── search/ (SearchScreen, SearchResultCard, SkeletonLoader)
│   ├── stats/ (StatsScreen, ProgresGoalsScreen)
│   ├── profile/ (ProfilScreen)
│   ├── settings/ (ReminderSettingsScreen)
│   ├── splash/ (SplashScreen)
│   └── theme/ (Color.kt, Theme.kt, Type.kt)
├── util/
│   ├── ProfilePictureManager.kt
│   ├── NotificationHelper.kt
│   ├── MealReminderScheduler.kt
│   ├── MealReminderReceiver.kt
│   └── BootReceiver.kt
└── assets/food_data.json (277KB - 1346 items)
```

## Design System

**Warna Utama:**
- Primary (Fresh Green): `#0F5238`
- Secondary (Vibrant Orange): `#9B4500`
- Tertiary (Red): `#713638`

**Typography:** Inter (body/headings), JetBrains Mono (labels)
**Spacing:** 8px grid base, 20px padding, 16px gutter
**Radius:** 16px cards, 24px hero, 12px buttons

Lihat `Aset/DESIGN.md` untuk detail lengkap.

## Quick Start

```bash
# Build
./gradlew assembleDebug

# Install ke device
./gradlew installDebug

# Testing
./gradlew testDebugUnitTest
./gradlew connectedDebugAndroidTest
```

## CLI Development (Tanpa Android Studio)

```bash
# Setup
adb devices
emulator -avd <name>

# Build & deploy
./gradlew assembleDebug --no-daemon
./gradlew installDebug
adb shell am start -n com.example.piringku/.MainActivity

# Debug
adb logcat | grep piringku
```

## Data

**Food Dataset:** Kaggle "Indonesian Food and Drink Nutrition Dataset" (1346 item, format JSON)
**Database:** Room SQLite dengan `fallbackToDestructiveMigration()` tiap versi berubah

## Development Notes

- **Arsitektur:** Single Activity + NavHost, bottom nav 4 rute (journal, stats, cari, profile)
- **Multi-user:** Room + UserPreferences untuk session switching
- **Foto profil:** Disimpan sebagai file `profile_{userId}.jpg`, Coil untuk loading
- **Target nutrisi:** Default 2000 kkal per hari, bisa di-custom via TargetEditSheet; reset ke default untuk hari non-today
- **Wajib uninstall sebelum build baru** tiap kali versi DB berubah (destructive migration)

## Recent Changes

- ✅ Multi-user support dengan Room DB (UserEntity, login/register, data diri)
- ✅ Barcode scanner removed (CameraX + MLKit dihapus)
- ✅ Password visibility toggle & confirm password di Register
- ✅ Back button di Register screen
- ✅ Profile photo loading bug fixed (LaunchedEffect key)
- ✅ Search bottom sheet back → kembali ke journal
- ✅ Date picker state fresh tiap dialog dibuka
- ✅ Food entry timestamp pakai selectedDate (bukan Instant.now())
- ✅ Target nutrisi reset per hari (2000 default untuk non-today)
- ✅ Performance fixes (recomposition, dispatcher, coroutine scope)

## License

MIT
