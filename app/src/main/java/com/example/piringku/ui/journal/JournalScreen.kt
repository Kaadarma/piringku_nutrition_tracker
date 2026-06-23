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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.piringku.data.FoodRepository
import com.example.piringku.data.JournalRepository
import com.example.piringku.model.FoodItem
import com.example.piringku.model.MealType
import com.example.piringku.ui.theme.BorderSubtle
import com.example.piringku.ui.theme.DataBlue
import com.example.piringku.ui.theme.EnergyOrange
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
    val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val today = LocalDate.now()
    val entries by repository.getEntriesByDate(today).collectAsState(initial = emptyList())
    val nutrition by repository.getDailyNutrition(today).collectAsState(initial = com.example.piringku.model.DailyNutrition())

    var showAddSheet by remember { mutableStateOf(false) }
    var selectedEntryId by remember { mutableLongStateOf(-1L) }
    var selectedFood by remember { mutableStateOf<FoodItem?>(null) }

    val showEditSheet = selectedEntryId > 0L
    val targetCalories = 2000f
    val targetProtein = 150f
    val targetFat = 65f
    val targetCarbs = 250f

    val remainingCalories = (targetCalories - nutrition.calories).coerceAtLeast(0f)
    val calorieProgress = (nutrition.calories / targetCalories).coerceAtMost(1f)

    val entryToEdit = if (showEditSheet) {
        entries.find { it.id == selectedEntryId }
    } else null

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
            item { DateTargetHeader(today = today, targetCalories = targetCalories.toInt()) }
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

                item {
                    MealSection(
                        title = mealType.displayName,
                        calories = mealCalories,
                        entries = mealEntries,
                        isEmpty = mealEntries.isEmpty(),
                        onEntryClick = { id -> selectedEntryId = id },
                        onAddClick = { showAddSheet = true },
                    )
                }
                item { Spacer(Modifier.height(12.dp)) }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            sheetState = addSheetState,
        ) {
            if (selectedFood != null) {
                FoodDetailSheet(
                    food = selectedFood!!,
                    onDismiss = { showAddSheet = false },
                    onAdded = {
                        showAddSheet = false
                        selectedFood = null
                    },
                )
            } else {
                com.example.piringku.ui.search.SearchScreen(
                    onFoodSelected = { food ->
                        selectedFood = food
                    },
                )
            }
        }
    }

    if (showEditSheet && entryToEdit != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedEntryId = -1 },
            sheetState = editSheetState,
        ) {
            UpdateDeleteSheet(
                entry = entryToEdit,
                onDismiss = { selectedEntryId = -1 },
                onUpdated = { selectedEntryId = -1 },
                onDeleted = { selectedEntryId = -1 },
            )
        }
    }
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
    today: LocalDate,
    targetCalories: Int,
) {
    val formatter = DateTimeFormatter.ofPattern("'Hari Ini, 'd MMM", Locale.forLanguageTag("id"))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = today.format(formatter),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "TARGET",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
            CircularProgressIndicator(
                progress = { calorieProgress },
                modifier = Modifier.size(192.dp),
                strokeWidth = 8.dp,
                trackColor = Color(0xFFE1E3DF),
                color = HealthGreen,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "%,d".format(remainingCalories),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurface,
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
private fun MealSection(
    title: String,
    calories: Float,
    entries: List<com.example.piringku.model.JournalEntry>,
    isEmpty: Boolean,
    onEntryClick: (Long) -> Unit,
    onAddClick: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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

        if (isEmpty) {
            Box(
                modifier = Modifier
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
        } else {
            entries.forEach { entry ->
                JournalEntryCard(
                    entry = entry,
                    onClick = { onEntryClick(entry.id) },
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
