package com.example.piringku.ui.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.LaunchedEffect
import com.example.piringku.data.UserPreferences
import com.example.piringku.data.FoodRepository
import com.example.piringku.data.JournalRepository
import com.example.piringku.data.TargetPreferences
import com.example.piringku.model.FoodItem
import com.example.piringku.model.MealType
import com.example.piringku.ui.theme.BorderSubtle
import com.example.piringku.ui.theme.DataBlue
import com.example.piringku.ui.theme.EnergyOrange
import com.example.piringku.ui.theme.ErrorRed
import com.example.piringku.ui.theme.HealthGreen
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen() {
    val context = LocalContext.current
    val repository = remember { JournalRepository.getInstance(context) }
    val foodRepository = remember { FoodRepository.getInstance(context) }
    val scope = rememberCoroutineScope()
    val userPrefs = remember { UserPreferences.getInstance(context) }
    var userId by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        userId = userPrefs.getUserId()
    }
    val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today) }
    var showDatePicker by remember { mutableStateOf(false) }

    val entries by repository.getEntriesByDate(selectedDate, userId).collectAsState(initial = emptyList())
    val nutrition by remember(entries) {
        derivedStateOf {
            com.example.piringku.model.DailyNutrition(
                calories = entries.sumOf { it.calories.toDouble() }.toFloat(),
                protein = entries.sumOf { it.protein.toDouble() }.toFloat(),
                fat = entries.sumOf { it.fat.toDouble() }.toFloat(),
                carbs = entries.sumOf { it.carbs.toDouble() }.toFloat(),
            )
        }
    }

    var showAddSheet by remember { mutableStateOf(false) }
    var selectedEntryId by remember { mutableLongStateOf(-1L) }
    var selectedFood by remember { mutableStateOf<FoodItem?>(null) }
    var addSheetMealType by remember { mutableStateOf(MealType.BREAKFAST) }

    val targetPrefs = remember { TargetPreferences.getInstance(context) }
    var targets by remember { mutableStateOf(targetPrefs.getTargets()) }
    var showTargetSheet by remember { mutableStateOf(false) }

    LaunchedEffect(selectedDate) {
        targets = if (selectedDate == today) {
            targetPrefs.getTargets()
        } else {
            com.example.piringku.data.DailyTargets()
        }
    }

    val showEditSheet = selectedEntryId > 0L
    val targetCalories = targets.calories
    val targetProtein = targets.protein
    val targetFat = targets.fat
    val targetCarbs = targets.carbs

    val remainingCalories = targetCalories - nutrition.calories
    val calorieProgress = if (targetCalories > 0f) (nutrition.calories / targetCalories).coerceAtMost(1f) else 0f

    val entryToEdit by remember {
        derivedStateOf {
            if (selectedEntryId > 0L) entries.find { it.id == selectedEntryId } else null
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Makanan")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(padding),
        ) {
            item { AppHeader() }
            item {
                DateTargetHeader(
                    selectedDate = selectedDate,
                    targetCalories = targetCalories.toInt(),
                    onPreviousDay = { selectedDate = selectedDate.minusDays(1) },
                    onNextDay = { selectedDate = selectedDate.plusDays(1) },
                    onDateClick = { showDatePicker = true },
                    onTargetClick = { showTargetSheet = true },
                )
            }
            item {
                HeroSection(
                    remainingCalories = remainingCalories.toInt(),
                    calorieProgress = calorieProgress,
                    nutrition = nutrition,
                    targetProtein = targetProtein,
                    targetFat = targetFat,
                    targetCarbs = targetCarbs,
                )
            }
            item { Spacer(Modifier.height(16.dp)) }

            val entriesByMeal = entries.groupBy { it.mealType }
            val mealTypes = listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER, MealType.SNACK)

            mealTypes.forEach { mealType ->
                val mealEntries = entriesByMeal[mealType] ?: emptyList()
                val mealCalories = mealEntries.sumOf { it.calories.toDouble() }.toFloat()

                if (mealEntries.isEmpty()) {
                    item(key = "empty_${mealType}") {
                        MealSection(
                            title = mealType.displayName,
                            calories = mealCalories,
                            entries = mealEntries,
                            isEmpty = true,
                            onEntryClick = {},
                            onAddClick = {
                                addSheetMealType = mealType
                                showAddSheet = true
                            },
                        )
                    }
                } else {
                    item(key = "header_${mealType}") {
                        MealHeader(title = mealType.displayName, calories = mealCalories)
                    }
                    items(items = mealEntries, key = { entry -> entry.id }) { entry ->
                        JournalEntryCard(
                            entry = entry,
                            onClick = { selectedEntryId = entry.id },
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )
                    }
                }
                item { Spacer(Modifier.height(12.dp)) }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false; selectedFood = null },
            sheetState = addSheetState,
        ) {
            if (selectedFood != null) {
                FoodDetailSheet(
                    food = selectedFood!!,
                    userId = userId,
                    entryDate = selectedDate,
                    onDismiss = { showAddSheet = false; selectedFood = null },
                    onAdded = {
                        showAddSheet = false
                        selectedFood = null
                    },
                    onBack = { selectedFood = null },
                    initialMealType = addSheetMealType,
                )
            } else {
                com.example.piringku.ui.search.SearchScreen(
                    onBack = { showAddSheet = false },
                    onFoodSelected = { food ->
                        selectedFood = food
                    },
                )
            }
        }
    }

    if (showDatePicker) {
        val dpState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
                .atStartOfDay(java.time.ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dpState.selectedDateMillis?.let { millis ->
                        selectedDate = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneOffset.UTC)
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("Pilih") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
            },
        ) {
            DatePicker(state = dpState)
        }
    }

    if (showTargetSheet) {
        TargetEditSheet(
            targets = targets,
            onDismiss = { showTargetSheet = false },
            onSave = { newTargets ->
                targets = newTargets
                targetPrefs.saveTargets(newTargets)
                showTargetSheet = false
            },
        )
    }

    val editEntry = entryToEdit
    if (showEditSheet && editEntry != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedEntryId = -1 },
            sheetState = editSheetState,
        ) {
            UpdateDeleteSheet(
                entry = editEntry,
                onDismiss = { selectedEntryId = -1 },
                onUpdated = { selectedEntryId = -1 },
                onDeleted = { selectedEntryId = -1 },
            )
        }
    }
}

@Composable
private fun TargetEditSheet(
    targets: com.example.piringku.data.DailyTargets,
    onDismiss: () -> Unit,
    onSave: (com.example.piringku.data.DailyTargets) -> Unit,
) {
    val defaultTargets = com.example.piringku.data.DailyTargets()
    var caloriesText by remember { mutableStateOf(targets.calories.toInt().toString()) }
    var proteinText by remember { mutableStateOf(targets.protein.toInt().toString()) }
    var fatText by remember { mutableStateOf(targets.fat.toInt().toString()) }
    var carbsText by remember { mutableStateOf(targets.carbs.toInt().toString()) }

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            ) {
                Text(
                    text = "Target Harian",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Sesuaikan target nutrisi harianmu",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        TargetField(label = "Kalori", suffix = "kkal", value = caloriesText) {
                            caloriesText = it.filter { c -> c.isDigit() }.take(4)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        TargetField(label = "Protein", suffix = "g", value = proteinText) {
                            proteinText = it.filter { c -> c.isDigit() }.take(3)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        TargetField(label = "Lemak", suffix = "g", value = fatText) {
                            fatText = it.filter { c -> c.isDigit() }.take(3)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        TargetField(label = "Karbo", suffix = "g", value = carbsText) {
                            carbsText = it.filter { c -> c.isDigit() }.take(4)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        caloriesText = defaultTargets.calories.toInt().toString()
                        proteinText = defaultTargets.protein.toInt().toString()
                        fatText = defaultTargets.fat.toInt().toString()
                        carbsText = defaultTargets.carbs.toInt().toString()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Rekomendasi")
                }

                Spacer(Modifier.height(12.dp))

                androidx.compose.material3.Button(
                    onClick = {
                        val cals = caloriesText.toFloatOrNull() ?: return@Button
                        val prot = proteinText.toFloatOrNull() ?: return@Button
                        val ft = fatText.toFloatOrNull() ?: return@Button
                        val crb = carbsText.toFloatOrNull() ?: return@Button
                        onSave(com.example.piringku.data.DailyTargets(cals, prot, ft, crb))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Text("Simpan", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun TargetField(
    label: String,
    suffix: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
    )
    androidx.compose.material3.OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        suffix = { Text(suffix, style = MaterialTheme.typography.bodySmall) },
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
        ),
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        ),
    )
}

@Composable
private fun AppHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "PIRINGKU",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun DateTargetHeader(
    selectedDate: LocalDate,
    targetCalories: Int,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onDateClick: () -> Unit,
    onTargetClick: () -> Unit,
) {
    val today = LocalDate.now()
    val formatter = if (selectedDate == today) {
        DateTimeFormatter.ofPattern("'Hari Ini, 'd MMM", Locale.forLanguageTag("id"))
    } else {
        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("id"))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onDateClick() },
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(4.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Hari sebelumnya",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onPreviousDay() },
            )

            Text(
                text = selectedDate.format(formatter),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Hari berikutnya",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onNextDay() },
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.weight(1f))
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.clickable { onTargetClick() },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "TARGET",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Ubah target",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp),
                )
            }
            Text(
                text = "$targetCalories kkal",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun HeroSection(
    remainingCalories: Int,
    calorieProgress: Float,
    nutrition: com.example.piringku.model.DailyNutrition,
    targetProtein: Float,
    targetFat: Float,
    targetCarbs: Float,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(192.dp),
            contentAlignment = Alignment.Center,
        ) {
            val exceeded = remainingCalories < 0
            CircularProgressIndicator(
                progress = { calorieProgress },
                modifier = Modifier.size(192.dp),
                strokeWidth = 8.dp,
                trackColor = Color(0xFFE1E3DF),
                color = if (exceeded) ErrorRed else HealthGreen,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "%,d".format(remainingCalories),
                    style = MaterialTheme.typography.displayLarge,
                    color = if (exceeded) ErrorRed else MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Sisa Kalori",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        MacroLinearBar("Protein", nutrition.protein, targetProtein, DataBlue)
        Spacer(Modifier.height(12.dp))
        MacroLinearBar("Lemak", nutrition.fat, targetFat, EnergyOrange)
        Spacer(Modifier.height(12.dp))
        MacroLinearBar("Karbo", nutrition.carbs, targetCarbs, HealthGreen)
    }
}

@Composable
private fun MacroLinearBar(
    label: String,
    current: Float,
    target: Float,
    color: Color,
) {
    val progress = (current / target).coerceAtMost(1f)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "%.0fg / %.0fg".format(current, target),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color),
            )
        }
    }
}

@Composable
private fun MealHeader(
    title: String,
    calories: Float,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "%.0f kkal".format(calories),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun MealSection(
    title: String,
    calories: Float,
    entries: List<com.example.piringku.model.JournalEntry>,
    isEmpty: Boolean,
    onEntryClick: (Long) -> Unit,
    onAddClick: () -> Unit,
) {
    MealHeader(title = title, calories = calories)

    if (isEmpty) {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .clickable { onAddClick() }
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Tambah Menu",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
