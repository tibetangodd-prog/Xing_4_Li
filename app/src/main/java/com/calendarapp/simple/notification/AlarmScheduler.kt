package com.calendarapp.simple.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object AlarmScheduler {
    private const val REQUEST_CODE = 1001

    private fun buildPendingIntent(context: Context, flags: Int): PendingIntent? {
        val intent = Intent(context, HourlyAlarmReceiver::class.java)
        return PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flags)
    }

    /** 排程下一個整點（例如現在 3:20，就排 4:00）的鬧鐘 */
    fun scheduleNextHourlyAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, 1)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = buildPendingIntent(context, flags) ?: return

        val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

        if (canExact) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            // 沒有精準鬧鐘權限時，退回不精準的排程（系統可能會延後幾分鐘）
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun hasScheduledAlarm(context: Context): Boolean {
        val flags = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        return buildPendingIntent(context, flags) != null
    }

    /** 只有在還沒有排程時才建立，避免每次開 App 都把鬧鐘往後延 */
    fun ensureScheduled(context: Context) {
        if (!hasScheduledAlarm(context)) {
            scheduleNextHourlyAlarm(context)
        }
    }
}
