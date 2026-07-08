package com.example.piringku.ui.stats

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.piringku.data.JournalRepository
import com.example.piringku.data.TargetPreferences
import com.example.piringku.data.UserPreferences
import com.example.piringku.data.repository.UserProfile
import com.example.piringku.data.repository.UserRepository
import com.example.piringku.ui.theme.BorderSubtle
import com.example.piringku.ui.theme.DataBlue
import com.example.piringku.ui.theme.EnergyOrange
import com.example.piringku.ui.theme.ErrorRed
import com.example.piringku.ui.theme.HealthGreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

data class WeightDataPoint(
    val date: LocalDate,
    val weight: Float,
)

@Composable
fun ProgresGoalsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences.getInstance(context) }
    val userRepo = remember { UserRepository.getInstance(context) }
    val journalRepo = remember { JournalRepository.getInstance(context) }
    val targetPrefs = remember { TargetPreferences.getInstance(context) }
    val scope = rememberCoroutineScope()
    var profile by remember { mutableStateOf(UserProfile()) }
    var userId by remember { mutableStateOf(0L) }
    var weightHistory by remember { mutableStateOf<List<UserPreferences.WeightEntry>>(emptyList()) }
    var showWeightDialog by remember { mutableStateOf(false) }
    var weightChartPeriod by remember { mutableIntStateOf(0) } // 0 = Bulanan, 1 = Tahunan

    LaunchedEffect(Unit) {
        userId = prefs.getUserId()
    }

    LaunchedEffect(userId) {
        if (userId != 0L) {
            userRepo.getUserProfile(userId).collect { profile = it }
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            weightHistory = prefs.getWeightHistory()
        }
    }

    val today = remember { LocalDate.now() }

    val weightDataPoints = remember(weightHistory) {
        weightHistory.mapNotNull { entry ->
            try { WeightDataPoint(LocalDate.parse(entry.date), entry.weight) }
            catch (_: Exception) { null }
        }.sortedBy { it.date }
    }

    val chartStartDate = remember(weightChartPeriod) {
        when (weightChartPeriod) {
            0 -> today.minusMonths(1)
            else -> today.minusYears(1)
        }
    }

    val periodLabel = if (weightChartPeriod == 0) "1 Bulan" else "1 Tahun"
    val periodDays = if (weightChartPeriod == 0) 30 else 365

    val filteredData = remember(weightDataPoints, chartStartDate) {
        weightDataPoints.filter { !it.date.isBefore(chartStartDate) }
    }

    val targetCalories = remember { targetPrefs.getTargets().calories }

    val totalCalories by journalRepo.getCaloriesInRange(chartStartDate, today.plusDays(1), userId)
        .collectAsState(initial = 0f)

    val startWeight = remember(weightDataPoints) {
        val beforeStart = weightDataPoints.filter { !it.date.isBefore(chartStartDate) }
        beforeStart.firstOrNull()?.weight ?: profile.weight
    }

    val averageTargetDaily = targetCalories
    val expectedWeightFromCalories = startWeight + (totalCalories - averageTargetDaily * periodDays) / 7700f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
            Text(
                text = "Progress & Goals",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            WeightHeroSection(
                currentWeight = profile.weight,
                targetWeight = profile.targetWeight,
                expectedWeight = expectedWeightFromCalories,
                onRecordWeight = { showWeightDialog = true },
            )

            WeightChartSection(
                dataPoints = filteredData,
                periodLabel = periodLabel,
                selectedPeriod = weightChartPeriod,
                onPeriodChange = { weightChartPeriod = it },
                targetWeight = profile.targetWeight,
                projectedWeight = expectedWeightFromCalories,
            )

            CalorieSurplusSection(
                totalCalories = totalCalories,
                targetCalories = averageTargetDaily * periodDays,
                periodDays = periodDays,
                startWeight = startWeight,
                expectedWeight = expectedWeightFromCalories,
            )

            NutritionAdherenceSection()
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showWeightDialog) {
        WeightInputDialog(
            initialWeight = profile.weight,
            onDismiss = { showWeightDialog = false },
            onSave = { weight ->
                scope.launch(Dispatchers.IO) {
                    val entry = UserPreferences.WeightEntry(today.toString(), weight)
                    prefs.saveWeightEntry(entry)
                    userRepo.saveUser(
                        userId = userId,
                        name = profile.name,
                        email = profile.email,
                        height = profile.height,
                        weight = weight,
                        age = profile.age,
                        gender = profile.gender,
                        activityLevel = profile.activityLevel,
                        targetWeight = profile.targetWeight,
                        goalCalories = profile.goalCalories,
                        goalProtein = profile.goalProtein,
                        goalFat = profile.goalFat,
                        goalCarbs = profile.goalCarbs,
                    )
                    weightHistory = prefs.getWeightHistory()
                }
                showWeightDialog = false
            },
        )
    }
}

@Composable
private fun WeightInputDialog(
    initialWeight: Float,
    onDismiss: () -> Unit,
    onSave: (Float) -> Unit,
) {
    var weightText by remember { mutableStateOf("%.1f".format(initialWeight)) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Catat Berat Badan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Tanggal: ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Berat (Kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) { Text("Batal") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            weightText.toFloatOrNull()?.let { onSave(it) }
                        },
                    ) { Text("Simpan") }
                }
            }
        }
    }
}

@Composable
private fun WeightHeroSection(
    currentWeight: Float,
    targetWeight: Float,
    expectedWeight: Float,
    onRecordWeight: () -> Unit,
) {
    val weightDiff = targetWeight - currentWeight
    val trendIcon = if (weightDiff >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown
    val trendText = if (weightDiff >= 0) "+%.1f Kg".format(weightDiff) else "%.1f Kg".format(weightDiff)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 32.dp, y = (-32).dp)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                                Color.Transparent,
                            ),
                        ),
                        RoundedCornerShape(64.dp),
                    ),
            )

            Column {
                Text(
                    text = "BERAT SAAT INI",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp,
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "%.0f".format(currentWeight),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "Kg",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                    )
                    Spacer(Modifier.weight(1f))
                    Row(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(20.dp),
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            trendIcon,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = trendText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Flag,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Target: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "%.0f Kg".format(targetWeight),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = " (Kekurangan %.0f Kg)".format(kotlin.math.abs(weightDiff)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = EnergyOrange,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Proyeksi berdasarkan kalori: ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "%.1f Kg".format(expectedWeight),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = EnergyOrange,
                    )
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onRecordWeight,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Catat Berat Hari Ini")
                }
            }
        }
    }
}

@Composable
private fun WeightChartSection(
    dataPoints: List<WeightDataPoint>,
    periodLabel: String,
    selectedPeriod: Int,
    onPeriodChange: (Int) -> Unit,
    targetWeight: Float,
    projectedWeight: Float,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = CardDefaults.outlinedCardBorder(),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Tren Berat Badan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceContainerLow,
                            RoundedCornerShape(8.dp),
                        )
                        .padding(4.dp),
                ) {
                    listOf("1 Bln", "1 Thn").forEachIndexed { index, label ->
                        Text(
                            text = label,
                            modifier = Modifier
                                .clickable { onPeriodChange(index) }
                                .background(
                                    if (index == selectedPeriod) MaterialTheme.colorScheme.surfaceContainerLowest
                                    else Color.Transparent,
                                    RoundedCornerShape(6.dp),
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (index == selectedPeriod) FontWeight.Bold else FontWeight.Normal,
                            color = if (index == selectedPeriod) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "$periodLabel - ${dataPoints.size} catatan",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(20.dp))

            if (dataPoints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Belum ada data berat badan.\nCatat berat badanmu untuk melihat tren.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                WeightLineChart(
                    dataPoints = dataPoints,
                    targetWeight = targetWeight,
                    projectedWeight = projectedWeight,
                )
            }
        }
    }
}

@Composable
private fun WeightLineChart(
    dataPoints: List<WeightDataPoint>,
    targetWeight: Float,
    projectedWeight: Float,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = ErrorRed
    val gridColor = BorderSubtle
    val dotColor = MaterialTheme.colorScheme.surfaceContainerLowest
    val chartHeight = 200.dp

    val weights = dataPoints.map { it.weight }
    val minVal = (weights.minOrNull() ?: 40f).coerceAtMost(targetWeight).coerceAtMost(projectedWeight) - 5f
    val maxVal = (weights.maxOrNull() ?: 100f).coerceAtLeast(targetWeight).coerceAtLeast(projectedWeight) + 5f
    val range = (maxVal - minVal).coerceAtLeast(1f)

    val dateLabels = remember(dataPoints) {
        dataPoints.map { it.date.format(DateTimeFormatter.ofPattern("dd/MM")) }
    }

    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight),
        ) {
            val w = size.width
            val h = size.height
            val pad = 40f
            val chartW = w - pad * 2
            val chartH = h - pad * 2

            val gridLines = 4
            for (i in 0..gridLines) {
                val y = pad + chartH * i / gridLines
                drawLine(gridColor, Offset(pad, y), Offset(w - pad, y), strokeWidth = 1f)
            }

            val targetChartValue = targetWeight.coerceIn(minVal, maxVal)
            val targetY = pad + chartH * (maxVal - targetChartValue) / range
            val dashPath = Path().apply {
                moveTo(pad, targetY)
                lineTo(w - pad, targetY)
            }
            drawPath(
                dashPath,
                errorColor,
                style = Stroke(width = 1.5f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(8f, 4f))),
            )
            drawContext.canvas.nativeCanvas.drawText(
                "TARGET %.0f".format(targetWeight),
                w - pad - 70f,
                targetY - 6f,
                android.graphics.Paint().apply {
                    color = errorColor.hashCode()
                    textSize = 24f
                    isAntiAlias = true
                },
            )

            val projectedChartValue = projectedWeight.coerceIn(minVal, maxVal)
            val projectedY = pad + chartH * (maxVal - projectedChartValue) / range
            val projPath = Path().apply {
                moveTo(pad, projectedY)
                lineTo(w - pad, projectedY)
            }
            drawPath(
                projPath,
                EnergyOrange,
                style = Stroke(width = 1.5f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(4f, 4f))),
            )
            drawContext.canvas.nativeCanvas.drawText(
                "PROYEKSI %.0f".format(projectedWeight),
                w - pad - 76f,
                projectedY - 20f,
                android.graphics.Paint().apply {
                    color = EnergyOrange.hashCode()
                    textSize = 22f
                    isAntiAlias = true
                },
            )

            if (dataPoints.size >= 2) {
                val points = dataPoints.mapIndexed { i, dp ->
                    val x = pad + chartW * i / (dataPoints.size - 1).coerceAtLeast(1)
                    val y = pad + chartH * (maxVal - dp.weight) / range
                    Offset(x, y)
                }

                val areaPath = Path().apply {
                    moveTo(points[0].x, pad + chartH)
                    for (i in points.indices) {
                        val p = points[i]
                        if (i == 0) lineTo(p.x, p.y)
                        else {
                            val prev = points[i - 1]
                            val cpx = (prev.x + p.x) / 2f
                            cubicTo(cpx, prev.y, cpx, p.y, p.x, p.y)
                        }
                    }
                    lineTo(points.last().x, pad + chartH)
                    close()
                }
                drawPath(areaPath, primaryColor.copy(alpha = 0.08f))

                val linePath = Path().apply {
                    points.forEachIndexed { i, p ->
                        if (i == 0) moveTo(p.x, p.y)
                        else {
                            val prev = points[i - 1]
                            val cpx = (prev.x + p.x) / 2f
                            cubicTo(cpx, prev.y, cpx, p.y, p.x, p.y)
                        }
                    }
                }
                drawPath(
                    linePath,
                    primaryColor,
                    style = Stroke(3f, cap = StrokeCap.Round, join = StrokeJoin.Round),
                )

                points.forEach { p ->
                    drawCircle(primaryColor, 6f, p)
                    drawCircle(dotColor, 3f, p)
                }

                val lastP = points.last()
                drawCircle(DataBlue, 8f, lastP)
                drawCircle(dotColor, 4f, lastP)
            } else if (dataPoints.size == 1) {
                val p = Offset(pad + chartW / 2f, pad + chartH * (maxVal - dataPoints[0].weight) / range)
                drawCircle(primaryColor, 6f, p)
                drawCircle(dotColor, 3f, p)
            }
        }

        Spacer(Modifier.height(8.dp))

        if (dateLabels.size <= 7) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                dateLabels.forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            LegendDot(color = primaryColor, label = "Berat Aktual")
            LegendDot(color = errorColor, label = "Target")
            LegendDot(color = EnergyOrange, label = "Proyeksi Kalori")
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, RoundedCornerShape(4.dp)),
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp,
        )
    }
}

@Composable
private fun CalorieSurplusSection(
    totalCalories: Float,
    targetCalories: Float,
    periodDays: Int,
    startWeight: Float,
    expectedWeight: Float,
) {
    val surplus = totalCalories - targetCalories
    val surplusPerDay = surplus / periodDays.coerceAtLeast(1)
    val expectedChange = expectedWeight - startWeight

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = CardDefaults.outlinedCardBorder(),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "DAMPAK KALORI TERHADAP BERAT BADAN",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp,
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                StatItem("Total Kalori", "%.0f kcal".format(totalCalories), primary = true)
                StatItem("Target Kalori", "%.0f kcal".format(targetCalories), primary = true)
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val surplusColor = if (surplus > 0) ErrorRed else HealthGreen
                val surplusSign = if (surplus > 0) "+" else ""
                StatItem("Surplus/Defisit", "${surplusSign}%.0f kcal".format(surplus), color = surplusColor)
                StatItem("Rata-rata/hari", "${surplusSign}%.0f kcal".format(surplusPerDay), color = surplusColor)
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val changeColor = if (expectedChange > 0) ErrorRed else HealthGreen
                val changeSign = if (expectedChange > 0) "+" else ""
                StatItem("Berat Awal", "%.1f Kg".format(startWeight), primary = true)
                StatItem("Estimasi Akhir", "${changeSign}%.1f Kg".format(expectedWeight), color = changeColor)
            }
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(4.dp)),
            ) {
                val progress = ((surplusPerDay + 1000f) / 2000f).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(8.dp)
                        .background(
                            if (surplus > 0) ErrorRed else HealthGreen,
                            RoundedCornerShape(4.dp),
                        ),
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Estimasi berdasarkan 1 kg lemak tubuh ≈ 7.700 kcal. Belum mempertimbangkan faktor metabolisme dan aktivitas fisik.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                lineHeight = 18.sp,
            )
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, primary: Boolean = false, color: Color? = null) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color ?: if (primary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun NutritionAdherenceSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Kepatuhan Nutrisi",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
        CalorieBarChart()
        MacroProgressSection()
    }
}

@Composable
private fun CalorieBarChart() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = CardDefaults.outlinedCardBorder(),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "ASUPAN KALORI HARIAN (Kcal)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp,
            )
            Spacer(Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth().height(128.dp)) {
                val heights = listOf(70f, 55f, 85f, 95f, 40f, 0f, 0f)
                val barColors = listOf(
                    HealthGreen, HealthGreen, HealthGreen,
                    MaterialTheme.colorScheme.primaryContainer, HealthGreen,
                    MaterialTheme.colorScheme.surfaceContainerHigh,
                    MaterialTheme.colorScheme.surfaceContainerHigh,
                )

                val targetLineY = (128f * 0.6f).dp
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .align(Alignment.TopStart)
                        .padding(top = targetLineY)
                        .background(Color.Transparent),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.dp)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                RoundedCornerShape(0.dp),
                            ),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    heights.forEachIndexed { i, h ->
                        if (h > 0f) {
                            Column(
                                modifier = Modifier.width(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(h.dp)
                                        .background(
                                            barColors[i],
                                            RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
                                        ),
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height(0.dp)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(4.dp)),
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                listOf("S", "S", "R", "K", "J", "S", "M").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun MacroProgressSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = CardDefaults.outlinedCardBorder(),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "PROGRES MAKRONUTRISI MINGGUAN",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp,
            )
            Spacer(Modifier.height(16.dp))
            MacroBar("Protein", 85, DataBlue)
            Spacer(Modifier.height(12.dp))
            MacroBar("Karbohidrat", 62, HealthGreen)
            Spacer(Modifier.height(12.dp))
            MacroBar("Lemak", 110, MaterialTheme.colorScheme.primaryContainer)
        }
    }
}

@Composable
private fun MacroBar(label: String, percent: Int, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = color,
            )
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceContainerLow,
                    RoundedCornerShape(20.dp),
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percent.toFloat() / 100f)
                    .height(8.dp)
                    .background(color, RoundedCornerShape(20.dp)),
            )
        }
    }
}
