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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.piringku.ui.theme.BorderSubtle
import com.example.piringku.ui.theme.DataBlue
import com.example.piringku.ui.theme.ErrorRed
import com.example.piringku.ui.theme.HealthGreen
import com.example.piringku.ui.theme.EnergyOrange

@Composable
fun StatsScreen() {
    var selectedPeriod by remember { mutableIntStateOf(0) }

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

            WeeklySummaryCard()

            Spacer(Modifier.height(16.dp))

            DailyCalorieChart()

            Spacer(Modifier.height(16.dp))

            MacroRingChart()

            Spacer(Modifier.height(24.dp))

            NutrientAchievementSection()

            Spacer(Modifier.height(24.dp))

            MostConsumedSection()

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
private fun WeeklySummaryCard() {
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
                        text = "WEEKLY SUMMARY",
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
                        .background(Color(0xFFB1F0CE))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "\u2193",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                    )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Down 5%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "1,850",
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
                text = "Daily Average vs 2,000 kcal Target",
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
                        .fillMaxWidth(0.925f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(HealthGreen),
                )
            }
        }
    }
}

@Composable
private fun DailyCalorieChart() {
    val dayLabels = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    val barHeights = listOf(0.65f, 0.85f, 0.40f, 0.95f, 0.70f, 1.0f, 0.55f)

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
                dayLabels.forEachIndexed { index, label ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((barHeights[index] * 192).dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFB1F0CE)),
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = label,
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
private fun MacroRingChart() {
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
                        topLeft = androidx.compose.ui.geometry.Offset(
                            topLeft.x, topLeft.y
                        ),
                        size = arcSize,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth),
                    )

                    drawArc(
                        color = HealthGreen,
                        startAngle = -90f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = androidx.compose.ui.geometry.Offset(
                            topLeft.x, topLeft.y
                        ),
                        size = arcSize,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth),
                    )

                    drawArc(
                        color = DataBlue,
                        startAngle = 90f,
                        sweepAngle = 108f,
                        useCenter = false,
                        topLeft = androidx.compose.ui.geometry.Offset(
                            topLeft.x, topLeft.y
                        ),
                        size = arcSize,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth),
                    )

                    drawArc(
                        color = EnergyOrange,
                        startAngle = 198f,
                        sweepAngle = 72f,
                        useCenter = false,
                        topLeft = androidx.compose.ui.geometry.Offset(
                            topLeft.x, topLeft.y
                        ),
                        size = arcSize,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth),
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Weekly",
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
                LegendItem(color = HealthGreen, label = "CARBS", value = "50%")
                LegendItem(color = DataBlue, label = "PROT", value = "30%")
                LegendItem(color = EnergyOrange, label = "FAT", value = "20%")
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
private fun NutrientAchievementSection() {
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
            goal = "120g / day",
            percentage = "84%",
            progress = 0.84f,
            barColor = DataBlue,
        )
        Spacer(Modifier.height(8.dp))
        NutrientCard(
            icon = "\uD83E\uDDC1",
            iconBg = Color(0xFFE8F5E9),
            iconColor = HealthGreen,
            name = "Carbohydrates",
            goal = "250g / day",
            percentage = "95%",
            progress = 0.95f,
            barColor = HealthGreen,
        )
        Spacer(Modifier.height(8.dp))
        NutrientCard(
            icon = "\uD83D\uDCA7",
            iconBg = Color(0xFFFFF3E0),
            iconColor = EnergyOrange,
            name = "Fat",
            goal = "65g / day",
            percentage = "110%",
            progress = 1f,
            barColor = ErrorRed,
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
    percentage: String,
    progress: Float,
    barColor: Color,
) {
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
                    text = percentage,
                    style = MaterialTheme.typography.labelLarge,
                    color = barColor,
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
                        .background(barColor),
                )
            }
        }
    }
}

@Composable
private fun MostConsumedSection() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "Most Consumed Food",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(16.dp))

        MostConsumedCard(
            image = "https://lh3.googleusercontent.com/aida-public/AB6AXuAbf0CcDivgsEgxhz8ew7g6p8dVMZKEUXO5MWLjaUwcTpP4zi34NkPfKYKIdwrmT2MqQe1k8PoLpejAjf8-B_kXpm5AyYYty_fFLxbOmwmYdlnKS2GbsrlsBciyN7tD9cOw98wFm41pu6AW2DuFfZTPzksqXy1ybi3I8Pn6HOJCT0iJvTf8j4xcj9_M24E35kYHsL-E1lblvOsAQhu-TDOfZTZrkMjzyrIh_rnjsCQS5-8wxADYLtdaxjK6egMCUC7tLXWshnH3PoM",
            name = "Nasi Putih",
            frequency = "12 Times this week",
            kcal = "240 kcal",
        )
        Spacer(Modifier.height(8.dp))
        MostConsumedCard(
            image = "https://lh3.googleusercontent.com/aida-public/AB6AXuCoLUyNf39dVMoWsNGCELeG5tXKuxrSLeq7E5DN9OgJBRuHtNxltEqTq0aE5zLot8HE0qQFUDQQBT9Wz1lJnevGk047-XbjMeIk8eakzHmD9pvPkHLG_yJcD2JBYi7WLfrE6ec3Rtl5U0Osls8Ie4410b7Avin2AP2Kg6nHAMLKyNOi3tjSbLutlYDzpQsyoLUiDYtRXILQh4iEcnrNA1T71M0REbmNXo-UGtOn73JjbUMNJRG5b72Sq9hBJD3G0uT6E9SumFdDIDE",
            name = "Dada Ayam",
            frequency = "8 Times this week",
            kcal = "165 kcal",
        )
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
