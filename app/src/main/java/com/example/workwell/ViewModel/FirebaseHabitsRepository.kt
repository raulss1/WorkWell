package com.example.workwell.ViewModel
import Habit
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

class FirebaseHabitsRepository(
    private val habitFacade: HabitsFacade
) : HabitsRepository {
    override suspend fun getHabits(): List<Habit> = habitFacade.getHabits()

    override fun decodeBase64ToBitmap(base64Str: String?): ImageBitmap? {
        return try {
            if (base64Str.isNullOrBlank()) return null
            val decodedBytes = Base64.decode(base64Str.trim(), Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}