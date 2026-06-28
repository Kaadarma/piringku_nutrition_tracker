package com.example.piringku.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.piringku.model.MealType

class MealReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val mealTypeName = intent.getStringExtra(EXTRA_MEAL_TYPE) ?: return
        val mealType = try {
            MealType.valueOf(mealTypeName)
        } catch (_: IllegalArgumentException) { return }

        NotificationHelper.createChannel(context)
        NotificationHelper.showMealReminder(
            context = context,
            title = mealType.notifTitle,
            body = mealType.notifBody,
            notificationId = mealType.notifId,
        )
    }

    companion object {
        const val EXTRA_MEAL_TYPE = "meal_type"
    }
}
