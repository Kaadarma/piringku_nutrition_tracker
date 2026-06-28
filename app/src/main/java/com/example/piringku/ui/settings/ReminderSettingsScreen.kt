package com.example.piringku.ui.settings

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.piringku.data.ReminderPreferences
import com.example.piringku.model.MealType
import com.example.piringku.util.MealReminderScheduler
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderSettingsScreen(
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val prefs = remember { ReminderPreferences.getInstance(context) }
    val configs = remember {
        MealType.entries.associateWith { mutableStateOf(prefs.getReminder(it)) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengingat Makan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Atur jadwal pengingat makan harian",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                ),
                elevation = CardDefaults.cardElevation(0.dp),
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    MealType.entries.forEachIndexed { index, mealType ->
                        if (index > 0) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = com.example.piringku.ui.theme.BorderSubtle,
                            )
                        }
                        ReminderRow(
                            mealType = mealType,
                            config = configs[mealType]!!.value,
                            onTimeChanged = { hour, minute ->
                                val newConfig = configs[mealType]!!.value.copy(hour = hour, minute = minute)
                                configs[mealType]!!.value = newConfig
                                prefs.setReminder(mealType, newConfig)
                                MealReminderScheduler.schedule(context, mealType, hour, minute)
                            },
                            onToggle = { enabled ->
                                val newConfig = configs[mealType]!!.value.copy(enabled = enabled)
                                configs[mealType]!!.value = newConfig
                                prefs.setReminder(mealType, newConfig)
                                if (enabled) {
                                    MealReminderScheduler.schedule(
                                        context, mealType, newConfig.hour, newConfig.minute,
                                    )
                                } else {
                                    MealReminderScheduler.cancel(context, mealType)
                                }
                            },
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Notifikasi akan dikirim setiap hari pada jam yang dipilih.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ReminderRow(
    mealType: MealType,
    config: com.example.piringku.data.ReminderConfig,
    onTimeChanged: (Int, Int) -> Unit,
    onToggle: (Boolean) -> Unit,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(when (mealType) {
                    MealType.BREAKFAST -> MaterialTheme.colorScheme.primaryContainer
                    MealType.LUNCH -> MaterialTheme.colorScheme.secondaryContainer
                    MealType.DINNER -> MaterialTheme.colorScheme.tertiaryContainer
                    MealType.SNACK -> MaterialTheme.colorScheme.errorContainer
                }),
            contentAlignment = Alignment.Center,
        ) {
            val icon = when (mealType) {
                MealType.BREAKFAST -> "\u2600\uFE0F"
                MealType.LUNCH -> "\uD83C\uDF24\uFE0F"
                MealType.DINNER -> "\uD83C\uDF19"
                MealType.SNACK -> "\uD83C\uDF1D"
            }
            Text(text = icon, fontSize = MaterialTheme.typography.titleMedium.fontSize)
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = mealType.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (config.enabled) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "%02d:%02d".format(config.hour, config.minute),
                style = MaterialTheme.typography.bodySmall,
                color = if (config.enabled) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable(enabled = config.enabled) {
                    TimePickerDialog(
                        context,
                        { _, hour, minute -> onTimeChanged(hour, minute) },
                        config.hour, config.minute, true,
                    ).show()
                },
            )
        }
        Switch(
            checked = config.enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        )
    }
}
