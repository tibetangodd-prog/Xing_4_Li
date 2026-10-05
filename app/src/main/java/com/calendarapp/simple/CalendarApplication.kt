package com.calendarapp.simple

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class CalendarApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "整點提醒",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "每個整點提醒你檢查行事曆（無聲無震動）"
                setSound(null, null)
                enableVibration(false)
                vibrationPattern = null
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "hourly_reminder_channel"
    }
}
