package com.example.workwell.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.workwell.Model.UserProviderFirebase
import com.google.firebase.auth.FirebaseAuth
import java.util.Date

data class RoutineData(
    val name: String,
    val startTime: Date,
    val endTime: Date,
    val type: String,       // "Laboral" o "Casual"
    val priority: String,   // "Alta", "Media", "Baja"
    // Los avisos pueden ser nulos (null) si el switch está apagado
    val eatReminderMinutes: String?,
    val standUpReminderMinutes: String?,
    val stretchReminderMinutes: String?
)

class CreateRoutineViewModel : ViewModel() {

    suspend fun createRoutine(routine: RoutineData) {
        Log.d("ViewModel", "Guardando rutina: ${routine.name}")

        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        val provider = UserProviderFirebase()
        provider.createUserTask(
            task = routine.name,
            userId = currentUserId,
            startDate = routine.startTime,
            endDate = routine.endTime,
            type = routine.type,
            priority = routine.priority,
            eatNotificationTime = routine.eatReminderMinutes ?: "",
            standNotificationTime = routine.standUpReminderMinutes ?: "",
            strechNotificationTime = routine.stretchReminderMinutes ?: ""
        )
    }

}