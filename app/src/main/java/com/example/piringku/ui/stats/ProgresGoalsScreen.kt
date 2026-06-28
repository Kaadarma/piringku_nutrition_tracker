package com.example.piringku.ui.stats

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.piringku.data.repository.UserProfile
import com.example.piringku.data.repository.UserRepository
import com.example.piringku.ui.theme.BorderSubtle
import com.example.piringku.ui.theme.DataBlue
import com.example.piringku.ui.theme.HealthGreen
import com.example.piringku.ui.theme.SecondaryContainer

@Composable
fun ProgresGoalsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val userRepo = remember { UserRepository.getInstance(context) }
    val profile by userRepo.userProfile.collectAsState(initial = UserProfile())

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
            )
            WeightChartSection(targetWeight = profile.targetWeight)
            NutritionAdherenceSection()
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun WeightHeroSection(
    currentWeight: Float,
    targetWeight: Float,
) {
    val weightDiff = targetWeight - currentWeight
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
                            Icons.Default.TrendingDown,
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
                        text = " (Kekurangan %.0f Kg lagi)".format(kotlin.math.abs(weightDiff)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {},
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
private fun WeightChartSection(targetWeight: Float = 68f) {
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
                    listOf("1 Mgg", "1 Bln", "3 Bln").forEach { label ->
                        Text(
                            text = label,
                            modifier = Modifier
                                .background(
                                    if (label == "1 Mgg") MaterialTheme.colorScheme.surfaceContainerLowest
                                    else Color.Transparent,
                                    RoundedCornerShape(6.dp),
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (label == "1 Mgg") FontWeight.Bold else FontWeight.Normal,
                            color = if (label == "1 Mgg") MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))

            val primaryColor = MaterialTheme.colorScheme.primary
            val errorColor = MaterialTheme.colorScheme.error
            val gridColor = BorderSubtle
            val dotColor = MaterialTheme.colorScheme.surfaceContainerLowest
            val chartHeight = 200.dp

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

                val data = listOf(40f, 45f, 60f, 85f, 75f, 100f, 110f)
                val minVal = 0f
                val maxVal = 140f

                val gridLines = 4
                val lineColor = gridColor
                for (i in 0..gridLines) {
                    val y = pad + chartH * i / gridLines
                    drawLine(lineColor, Offset(pad, y), Offset(w - pad, y), strokeWidth = 1f)
                }

                val targetChartValue = targetWeight.coerceIn(40f, 140f)
                val targetY = pad + chartH * (maxVal - targetChartValue) / maxVal
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
                    w - pad - 68f,
                    targetY - 6f,
                    android.graphics.Paint().apply {
                        color = errorColor.hashCode()
                        textSize = 24f
                        isAntiAlias = true
                    },
                )

                val points = data.mapIndexed { i, v ->
                    Offset(pad + chartW * i / (data.size - 1), pad + chartH * (maxVal - v) / maxVal)
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
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                listOf("SEN", "SEL", "RAB", "KAM", "JUM", "SAB", "MIN").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
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
                    SecondaryContainer, HealthGreen,
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
            MacroBar("Lemak", 110, SecondaryContainer)
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
