package com.example.piringku.ui.stats

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.piringku.data.UserPreferences
import coil.compose.AsyncImage
import com.example.piringku.data.DailyTargets
import com.example.piringku.data.JournalRepository
import com.example.piringku.data.TargetPreferences
import com.example.piringku.model.JournalEntry
import com.example.piringku.ui.theme.BorderSubtle
import com.example.piringku.ui.theme.DataBlue
import com.example.piringku.ui.theme.ErrorRed
import com.example.piringku.ui.theme.HealthGreen
import com.example.piringku.ui.theme.EnergyOrange
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

// ---------------------------------------------------------------------------
// Data holder murni (bukan Composable) hasil olahan dari JournalEntry asli.
// ---------------------------------------------------------------------------

private data class DayBar(val label: String, val calories: Float)

private data class MostConsumedItem(
    val name: String,
    val imageUrl: String,
    val timesInPeriod: Int,
    val caloriesPerServe: Float,
)

private data class StatsSummary(
    val averageCalories: Float = 0f,
    val averageProtein: Float = 0f,
    val averageFat: Float = 0f,
    val averageCarbs: Float = 0f,
    val proteinRatio: Float = 0f,
    val carbsRatio: Float = 0f,
    val fatRatio: Float = 0f,
    val mostConsumed: List<MostConsumedItem> = emptyList(),
)

private fun computeStatsSummary(entries: List<JournalEntry>, periodDays: Int): StatsSummary {
    if (entries.isEmpty()) return StatsSummary()

    val totalCalories = entries.sumOf { it.calories.toDouble() }.toFloat()
    val totalProtein = entries.sumOf { it.protein.toDouble() }.toFloat()
    val totalFat = entries.sumOf { it.fat.toDouble() }.toFloat()
    val totalCarbs = entries.sumOf { it.carbs.toDouble() }.toFloat()

    // Konversi gram -> kkal supaya rasio macro donut chart akurat secara nutrisi
    // (protein & karbo = 4 kkal/gram, lemak = 9 kkal/gram).
    val proteinKcal = totalProtein * 4f
    val carbsKcal = totalCarbs * 4f
    val fatKcal = totalFat * 9f
    val totalMacroKcal = (proteinKcal + carbsKcal + fatKcal).coerceAtLeast(1f)

    val mostConsumed = entries
        .groupBy { it.foodName }
        .map { (name, group) ->
            MostConsumedItem(
                name = name,
                imageUrl = group.firstOrNull { it.imageUrl.isNotBlank() }?.imageUrl ?: "",
                timesInPeriod = group.size,
                caloriesPerServe = group.sumOf { it.calories.toDouble() }.toFloat() / group.size,
            )
        }
        .sortedByDescending { it.timesInPeriod }
        .take(3)

    return StatsSummary(
        averageCalories = totalCalories / periodDays,
        averageProtein = totalProtein / periodDays,
        averageFat = totalFat / periodDays,
        averageCarbs = totalCarbs / periodDays,
        proteinRatio = proteinKcal / totalMacroKcal,
        carbsRatio = carbsKcal / totalMacroKcal,
        fatRatio = fatKcal / totalMacroKcal,
        mostConsumed = mostConsumed,
    )
}

private fun computeDailyBars(entries: List<JournalEntry>, startDate: LocalDate): List<DayBar> {
    val caloriesByDate: Map<LocalDate, Float> = entries
        .groupBy { it.timestamp.atZone(ZoneOffset.UTC).toLocalDate() }
        .mapValues { (_, group) -> group.sumOf { it.calories.toDouble() }.toFloat() }

    return (0..6).map { offset ->
        val date = startDate.plusDays(offset.toLong())
        val label = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).uppercase()
        DayBar(label = label, calories = caloriesByDate[date] ?: 0f)
    }
}

// ---------------------------------------------------------------------------
// Screen utama
// ---------------------------------------------------------------------------

@Composable
fun StatsScreen() {
    val context = LocalContext.current
    val journalRepository = remember { JournalRepository.getInstance(context) }
    val targetPrefs = remember { TargetPreferences.getInstance(context) }
    val targets: DailyTargets = remember { targetPrefs.getTargets() }
    val userPrefs = remember { UserPreferences.getInstance(context) }
    var userId by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        userId = userPrefs.getUserId()
    }

    var selectedPeriod by remember { mutableIntStateOf(0) } // 0 = Weekly, 1 = Monthly
    val periodDays = if (selectedPeriod == 0) 7 else 30
    val periodLabel = if (selectedPeriod == 0) "Weekly" else "Monthly"

    val today = remember { LocalDate.now() }
    val periodStart = remember(periodDays) { today.minusDays((periodDays - 1).toLong()) }
    val last7Start = remember { today.minusDays(6) }

    // Entries untuk ringkasan periode (rata-rata, rasio macro, most consumed)
    val periodEntries by journalRepository
        .getEntriesInRange(periodStart, today.plusDays(1), userId)
        .collectAsState(initial = emptyList())

    // Grafik batang harian selalu menampilkan 7 hari terakhir, apapun toggle-nya
    val last7Entries by journalRepository
        .getEntriesInRange(last7Start, today.plusDays(1), userId)
        .collectAsState(initial = emptyList())

    var summary by remember { mutableStateOf(StatsSummary()) }
    var dailyBars by remember { mutableStateOf(emptyList<DayBar>()) }
    LaunchedEffect(periodEntries, periodDays) {
        withContext(Dispatchers.Default) {
            summary = computeStatsSummary(periodEntries, periodDays)
        }
    }
    LaunchedEffect(last7Entries, last7Start) {
        withContext(Dispatchers.Default) {
            dailyBars = computeDailyBars(last7Entries, last7Start)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "Statistik Nutrisi",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        )

        PeriodSelector(
            selectedIndex = selectedPeriod,
            onSelect = { selectedPeriod = it },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(16.dp))

        WeeklySummaryCard(
            periodLabel = periodLabel,
            averageCalories = summary.averageCalories,
            targetCalories = targets.calories,
        )

        Spacer(Modifier.height(16.dp))

        DailyCalorieChart(bars = dailyBars, targetCalories = targets.calories)

        Spacer(Modifier.height(16.dp))

        MacroRingChart(
            periodLabel = periodLabel,
            proteinRatio = summary.proteinRatio,
            carbsRatio = summary.carbsRatio,
            fatRatio = summary.fatRatio,
        )

        Spacer(Modifier.height(24.dp))

        NutrientAchievementSection(
            averageProtein = summary.averageProtein,
            averageCarbs = summary.averageCarbs,
            averageFat = summary.averageFat,
            targets = targets,
        )

        Spacer(Modifier.height(24.dp))

        MostConsumedSection(items = summary.mostConsumed)

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun PeriodSelector(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(4.dp),
    ) {
        listOf("Weekly", "Monthly").forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onSelect(index) }
                    .padding(horizontal = 24.dp, vertical = 8.dp),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun WeeklySummaryCard(periodLabel: String, averageCalories: Float, targetCalories: Float) {
    val progress = if (targetCalories > 0f) (averageCalories / targetCalories).coerceIn(0f, 1f) else 0f
    val diffPercent = if (targetCalories > 0f) {
        (((averageCalories - targetCalories) / targetCalories) * 100).roundToInt()
    } else 0
    val isUnderTarget = diffPercent <= 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .padding(24.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column {
                    Text(
                        text = "${periodLabel.uppercase()} SUMMARY",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Average Performance",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isUnderTarget) Color(0xFFB1F0CE) else Color(0xFFFFDAD6))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isUnderTarget) "\u2193" else "\u2191",
                            color = if (isUnderTarget) MaterialTheme.colorScheme.primary else ErrorRed,
                            style = MaterialTheme.typography.labelLarge,
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${if (isUnderTarget) "Down" else "Up"} ${abs(diffPercent)}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isUnderTarget) MaterialTheme.colorScheme.primary else ErrorRed,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${averageCalories.roundToInt()}",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = " kcal",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Text(
                text = "Daily Average vs ${targetCalories.roundToInt()} kcal Target",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(HealthGreen),
                )
            }
        }
    }
}

@Composable
private fun DailyCalorieChart(bars: List<DayBar>, targetCalories: Float) {
    val maxValue = (bars.maxOfOrNull { it.calories } ?: 0f)
        .coerceAtLeast(targetCalories)
        .coerceAtLeast(1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .padding(24.dp),
    ) {
        Column {
            Text(
                text = "Daily Calorie Intake",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                bars.forEach { bar ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(((bar.calories / maxValue).coerceIn(0f, 1f) * 192).dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFB1F0CE)),
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = bar.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroRingChart(periodLabel: String, proteinRatio: Float, carbsRatio: Float, fatRatio: Float) {
    val carbsSweep = carbsRatio * 360f
    val proteinSweep = proteinRatio * 360f
    val fatSweep = fatRatio * 360f
    val carbsStart = -90f
    val proteinStart = carbsStart + carbsSweep
    val fatStart = proteinStart + proteinSweep

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .padding(24.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Macro Distribution",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(160.dp)) {
                    val strokeWidth = 16.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val topLeft = androidx.compose.ui.geometry.Offset(
                        (size.width - radius * 2 - strokeWidth) / 2 + strokeWidth / 2,
                        (size.height - radius * 2 - strokeWidth) / 2 + strokeWidth / 2,
                    )
                    val arcSize = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)

                    drawArc(
                        color = Color(0xFFE9ECEF),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = androidx.compose.ui.geometry.Offset(topLeft.x, topLeft.y),
                        size = arcSize,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth),
                    )

                    if (carbsSweep > 0f) {
                        drawArc(
                            color = HealthGreen,
                            startAngle = carbsStart,
                            sweepAngle = carbsSweep,
                            useCenter = false,
                            topLeft = androidx.compose.ui.geometry.Offset(topLeft.x, topLeft.y),
                            size = arcSize,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth),
                        )
                    }

                    if (proteinSweep > 0f) {
                        drawArc(
                            color = DataBlue,
                            startAngle = proteinStart,
                            sweepAngle = proteinSweep,
                            useCenter = false,
                            topLeft = androidx.compose.ui.geometry.Offset(topLeft.x, topLeft.y),
                            size = arcSize,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth),
                        )
                    }

                    if (fatSweep > 0f) {
                        drawArc(
                            color = EnergyOrange,
                            startAngle = fatStart,
                            sweepAngle = fatSweep,
                            useCenter = false,
                            topLeft = androidx.compose.ui.geometry.Offset(topLeft.x, topLeft.y),
                            size = arcSize,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth),
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = periodLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Ratio",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                LegendItem(color = HealthGreen, label = "CARBS", value = "${(carbsRatio * 100).roundToInt()}%")
                LegendItem(color = DataBlue, label = "PROT", value = "${(proteinRatio * 100).roundToInt()}%")
                LegendItem(color = EnergyOrange, label = "FAT", value = "${(fatRatio * 100).roundToInt()}%")
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    value: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun NutrientAchievementSection(
    averageProtein: Float,
    averageCarbs: Float,
    averageFat: Float,
    targets: DailyTargets,
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "Nutrient Achievement",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(16.dp))

        NutrientCard(
            icon = "\uD83E\uDD5A",
            iconBg = Color(0xFFE8F5E9),
            iconColor = HealthGreen,
            name = "Protein",
            goal = "${targets.protein.roundToInt()}g / day",
            average = averageProtein,
            target = targets.protein,
            barColor = DataBlue,
        )
        Spacer(Modifier.height(8.dp))
        NutrientCard(
            icon = "\uD83E\uDDC1",
            iconBg = Color(0xFFE8F5E9),
            iconColor = HealthGreen,
            name = "Carbohydrates",
            goal = "${targets.carbs.roundToInt()}g / day",
            average = averageCarbs,
            target = targets.carbs,
            barColor = HealthGreen,
        )
        Spacer(Modifier.height(8.dp))
        NutrientCard(
            icon = "\uD83D\uDCA7",
            iconBg = Color(0xFFFFF3E0),
            iconColor = EnergyOrange,
            name = "Fat",
            goal = "${targets.fat.roundToInt()}g / day",
            average = averageFat,
            target = targets.fat,
            barColor = EnergyOrange,
        )
    }
}

@Composable
private fun NutrientCard(
    icon: String,
    iconBg: Color,
    iconColor: Color,
    name: String,
    goal: String,
    average: Float,
    target: Float,
    barColor: Color,
) {
    val rawPercent = if (target > 0f) (average / target) * 100 else 0f
    val percentageText = "${rawPercent.roundToInt()}%"
    val progress = (rawPercent / 100f).coerceIn(0f, 1f)
    val isOverTarget = rawPercent > 100f
    val displayBarColor = if (isOverTarget) ErrorRed else barColor
    val displayPercentColor = if (isOverTarget) ErrorRed else barColor

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(iconBg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = icon, fontSize = 20.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Goal: $goal",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Text(
                    text = percentageText,
                    style = MaterialTheme.typography.labelLarge,
                    color = displayPercentColor,
                )
            }
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(displayBarColor),
                )
            }
        }
    }
}

@Composable
private fun MostConsumedSection(items: List<MostConsumedItem>) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "Most Consumed Food",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(16.dp))

        if (items.isEmpty()) {
            Text(
                text = "Belum ada data. Yuk mulai catat makananmu di halaman Journal!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            items.forEachIndexed { index, item ->
                MostConsumedCard(
                    image = item.imageUrl,
                    name = item.name,
                    frequency = "${item.timesInPeriod}x periode ini",
                    kcal = "${item.caloriesPerServe.roundToInt()} kcal",
                )
                if (index != items.lastIndex) {
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun MostConsumedCard(
    image: String,
    name: String,
    frequency: String,
    kcal: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = image,
            contentDescription = name,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = frequency,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = kcal,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Per serve",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
            )
        }
    }
}