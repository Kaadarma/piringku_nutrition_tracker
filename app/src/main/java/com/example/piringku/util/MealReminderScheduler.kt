package com.example.piringku.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.example.piringku.data.ReminderPreferences
import com.example.piringku.model.MealType
import java.util.Calendar

object MealReminderScheduler {

    fun scheduleAll(context: Context) {
        val prefs = ReminderPreferences.getInstance(context)
        MealType.entries.forEach { mealType ->
            val config = prefs.getReminder(mealType)
            if (config.enabled) {
                schedule(context, mealType, config.hour, config.minute)
            } else {
                cancel(context, mealType)
            }
        }
    }

    fun schedule(context: Context, mealType: MealType, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MealReminderReceiver::class.java).apply {
            putExtra(MealReminderReceiver.EXTRA_MEAL_TYPE, mealType.name)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, mealType.notifId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val canScheduleExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            alarmManager.canScheduleExactAlarms()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && canScheduleExact) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent,
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent,
            )
        }
    }

    fun cancel(context: Context, mealType: MealType) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MealReminderReceiver::class.java).apply {
            putExtra(MealReminderReceiver.EXTRA_MEAL_TYPE, mealType.name)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, mealType.notifId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        alarmManager.cancel(pendingIntent)
    }

    fun cancelAll(context: Context) {
        MealType.entries.forEach { cancel(context, it) }
    }
}
