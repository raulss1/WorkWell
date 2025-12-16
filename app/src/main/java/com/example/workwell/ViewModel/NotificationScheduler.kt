package com.example.workwell.ViewModel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "routine_channel_id"
        const val CHANNEL_NAME = "Recordatorios de Rutina"
    }

    init {
        createChannel()
    }

    // Crea el canal (Obligatorio en Android 8+)
    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Canal para notificaciones de rutinas activas"
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Programa la notificación periódica
    fun scheduleRoutineNotification(
        uniqueWorkName: String, // ID único para esta tarea (ej: "beber_agua")
        taskName: String,
        startHour: Int, startMinute: Int,
        endHour: Int, endMinute: Int,
        repeatIntervalMinutes: Long = 15 // Mínimo es 15 minutos en Android
    ) {
        // Pasamos los datos al Worker
        val data = Data.Builder()
            .putString("TASK_NAME", taskName)
            .putInt("START_HOUR", startHour)
            .putInt("START_MINUTE", startMinute)
            .putInt("END_HOUR", endHour)
            .putInt("END_MINUTE", endMinute)
            .build()

        // Creamos la petición de trabajo periódico
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            repeatIntervalMinutes, TimeUnit.MINUTES
        )
            .setInputData(data)
            .build()

        // Encolamos el trabajo
        // UPDATE asegura que si cambias las horas, se actualice la tarea existente
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            uniqueWorkName,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    // Función para cancelar una notificación específica
    fun cancelNotification(uniqueWorkName: String) {
        WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
    }
}