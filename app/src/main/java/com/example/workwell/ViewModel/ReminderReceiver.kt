package com.example.workwell.ViewModel

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.workwell.R
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {

    @SuppressLint("ScheduleExactAlarm")
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val taskName = intent.getStringExtra("TASK_NAME") ?: return
        val startHour = intent.getIntExtra("START_HOUR", 0)
        val startMinute = intent.getIntExtra("START_MINUTE", 0)
        val endHour = intent.getIntExtra("END_HOUR", 23)
        val endMinute = intent.getIntExtra("END_MINUTE", 59)
        val repeatInterval = intent.getLongExtra("REPEAT_INTERVAL", 15)

        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        if (currentHour in startHour..endHour &&
            (currentHour != endHour || currentMinute <= endMinute)
        ) {
            // Mostrar notificación
            NotificationHelper.showNotification(context, taskName)
        }

        // Reprogramar la siguiente alarma
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val nextTrigger = System.currentTimeMillis() + repeatInterval * 60_000
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTrigger, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextTrigger, pendingIntent)
        }
    }
}
object NotificationHelper {

    fun showNotification(context: Context, taskName: String) {
        val builder = NotificationCompat.Builder(context, NotificationScheduler.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // icono de tu drawable
            .setContentTitle("Recordatorio de rutina")
            .setContentText(taskName)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
            }
            notify(taskName.hashCode(), builder.build())
        }
    }
}

