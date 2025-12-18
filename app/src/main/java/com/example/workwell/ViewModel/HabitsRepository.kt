package com.example.workwell.ViewModel
import Habit
import androidx.compose.ui.graphics.ImageBitmap

interface HabitsRepository {
    suspend fun getHabits(): List<Habit>
    fun decodeBase64ToBitmap(base64Str: String?): ImageBitmap?
}