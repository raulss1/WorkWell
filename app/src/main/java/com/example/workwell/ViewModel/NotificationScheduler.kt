package com.example.workwell.ViewModel

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "routine_channel_id"
        const val CHANNEL_NAME = "Recordatorios de Rutina"
    }

    init {
        createChannel()
    }

    // =========================
    // CANAL DE NOTIFICACIONES
    // =========================
    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para recordatorios de rutinas"
            }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    // =========================
    // API PÚBLICA
    // =========================
    fun scheduleRoutineNotification(
        uniqueWorkName: String,
        taskName: String,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
        repeatIntervalMinutes: Long
    ) {
        cancelNotification(uniqueWorkName) // Evitar duplicados

        if (repeatIntervalMinutes < 15) {
            scheduleWithAlarmManager(
                uniqueWorkName,
                taskName,
                startHour,
                startMinute,
                endHour,
                endMinute,
                repeatIntervalMinutes
            )
        } else {
            scheduleWithWorkManager(
                uniqueWorkName,
                taskName,
                startHour,
                startMinute,
                endHour,
                endMinute,
                repeatIntervalMinutes
            )
        }
    }

    // =========================
    // WORK MANAGER (>= 15 min)
    // =========================
    private fun scheduleWithWorkManager(
        uniqueWorkName: String,
        taskName: String,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
        repeatIntervalMinutes: Long
    ) {
        val data = workDataOf(
            "TASK_NAME" to taskName,
            "START_HOUR" to startHour,
            "START_MINUTE" to startMinute,
            "END_HOUR" to endHour,
            "END_MINUTE" to endMinute
        )

        val request =
            PeriodicWorkRequestBuilder<NotificationWorker>(
                repeatIntervalMinutes,
                TimeUnit.MINUTES
            )
                .setInputData(data)
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            uniqueWorkName,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    // =========================
    // ALARM MANAGER (< 15 min)
    // =========================
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleWithAlarmManager(
        uniqueWorkName: String,
        taskName: String,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
        repeatIntervalMinutes: Long
    ) {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("TASK_NAME", taskName)
            putExtra("START_HOUR", startHour)
            putExtra("START_MINUTE", startMinute)
            putExtra("END_HOUR", endHour)
            putExtra("END_MINUTE", endMinute)
            putExtra("REPEAT_INTERVAL", repeatIntervalMinutes)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            uniqueWorkName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + repeatIntervalMinutes * 60_000

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    // =========================
    // CANCELAR
    // =========================
    fun cancelNotification(uniqueWorkName: String) {
        // WorkManager
        WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)

        // AlarmManager
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            uniqueWorkName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}
