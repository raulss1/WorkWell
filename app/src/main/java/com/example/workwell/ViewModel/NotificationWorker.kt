package com.example.workwell.ViewModel

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.workwell.MainActivity
import java.time.LocalTime

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        // 1. Obtener los datos que pasamos al programar
        val startHour = inputData.getInt("START_HOUR", 0)
        val startMinute = inputData.getInt("START_MINUTE", 0)
        val endHour = inputData.getInt("END_HOUR", 23)
        val endMinute = inputData.getInt("END_MINUTE", 59)
        val taskName = inputData.getString("TASK_NAME") ?: "Recordatorio"

        // 2. Comprobar la hora actual
        if (isCurrentTimeInRange(startHour, startMinute, endHour, endMinute)) {
            showNotification(taskName)
        }

        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isCurrentTimeInRange(startH: Int, startM: Int, endH: Int, endM: Int): Boolean {
        val now = LocalTime.now()
        val start = LocalTime.of(startH, startM)
        val end = LocalTime.of(endH, endM)

        // Lógica simple: si start es antes que end (ej: 09:00 a 17:00)
        return if (start.isBefore(end)) {
            now.isAfter(start) && now.isBefore(end)
        } else {
            // Lógica nocturna: si start es después de end (ej: 22:00 a 06:00)
            now.isAfter(start) || now.isBefore(end)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(title: String) {
        val channelId = "routine_channel_id"

        // Verificar permiso en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return // No tenemos permiso, no podemos notificar
            }
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Recordatorio de Rutina")
            .setContentText("Es hora de: $title")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(applicationContext).notify(System.currentTimeMillis().toInt(), builder.build())
    }
}