# PIRINGKU - Work Log (Orang 3: UI & API Developer - Search Specialist)

## Identitas
- **Nama**: Orang 3
- **Role**: UI & API Developer (Search Specialist)
- **Tugas**: Pencarian Makanan (3.png) & Skeleton Loading (2.png)

---

## Tanggal: 23 Juni 2026

### Commit 1: `9e0a0be`
```
chore: add Indonesian food nutrition dataset CSV
```
**Detail**: Menambahkan dataset CSV dari Kaggle (1346 makanan/minuman Indonesia)

### Commit 2: `ad4e628`
```
feat: implementasi Search Screen dengan dataset nutrisi makanan Indonesia
```
**Detail**: Implementasi lengkap Search Screen

---

## File yang Dibuat/Diubah

### Baru (7 file)
| File | Deskripsi |
|---|---|
| `app/src/main/assets/food_data.json` | Dataset 1346 makanan Indonesia (JSON) |
| `app/src/main/java/com/example/piringku/model/FoodItem.kt` | Data class makanan |
| `app/src/main/java/com/example/piringku/data/FoodRepository.kt` | Load & search dari JSON |
| `app/src/main/java/com/example/piringku/ui/search/SearchScreen.kt` | Screen utama search |
| `app/src/main/java/com/example/piringku/ui/search/SearchResultCard.kt` | Card hasil pencarian |
| `app/src/main/java/com/example/piringku/ui/search/SkeletonLoader.kt` | Shimmer loading |
| `scripts/csv_to_json.ps1` | Script konversi CSV → JSON |

### Diubah (6 file)
| File | Perubahan |
|---|---|
| `Color.kt` | Fresh Green palette dari DESIGN.md |
| `Theme.kt` | Light color scheme baru, dynamic color off |
| `Type.kt` | Inter + JetBrains Mono typography |
| `libs.versions.toml` | +Gson, +Coil |
| `app/build.gradle.kts` | +Gson, +Coil dependencies |
| `MainScreens.kt` | Hapus placeholder SearchScreen |

---

## Arsitektur Search

```
SearchScreen
    │
    ├─ SearchBar (rounded-full, search icon, QR button)
    │
    ├─ SearchTabs (Pencarian API | Riwayat Saya | Barcode Scanner)
    │
    ├─ Content
    │   ├─ SkeletonLoader (shimmer saat loading)
    │   ├─ EmptyState (hint "Mulai ketik...")
    │   ├─ SearchResultCard[] (lazy list hasil pencarian)
    │   └─ "Makanan tidak ditemukan" (empty result)
    │
    └─ FoodRepository
        └─ loadFoods() → filter name.contains(query)
```

---

## Tab System
| Tab | Status | Keterangan |
|---|---|---|
| Pencarian API | Aktif | Search dari local dataset (1346 items) |
| Riwayat Saya | Placeholder | Menunggu integrasi Orang 1/4 |
| Barcode Scanner | Placeholder | "Coming Soon" |

---

## Dependencies Ditambahkan
| Library | Versi | Untuk |
|---|---|---|
| `com.google.code.gson:gson` | 2.11.0 | Parse JSON dataset |
| `io.coil-kt:coil-compose` | 2.7.0 | Async image loading |

---

## Dataset
- **Sumber**: Kaggle Indonesian Food and Drink Nutrition Dataset
- **Jumlah**: 1346 item
- **Kolom**: id, name, calories, proteins, fat, carbs, image
- **Format**: JSON (277KB)

---

## Push Status
```
Remote: https://github.com/ler1304/piringku_nutrition_tracker.git
Branch: main → origin/main
Status: Pushed successfully (2 commits)
```

---

## Catatan untuk Anggota Tim Lain

### Untuk Orang 1 (Journal):
- `onFoodSelected(food: FoodItem)` callback sudah tersedia di SearchScreen
- Tinggal di-hook ke SharedViewModel + Room database

### Untuk Orang 4 (Backend):
- `FoodRepository` sudah singleton, bisa diakses dari mana saja
- `FoodItem` data class sudah siap untuk Room entity
- JSON dataset di `app/src/main/assets/food_data.json`

---

## Rencana Selanjutnya
- [ ] Integrasi dengan SharedViewModel (Orang 4)
- [ ] Riwayat Saya tab (perlu Room database)
- [ ] QR Scanner functionality
- [ ] Infinite scroll / pagination untuk 1346 items
