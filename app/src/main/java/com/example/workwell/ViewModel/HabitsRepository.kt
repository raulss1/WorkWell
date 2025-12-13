package com.example.workwell.ViewModel

import Habit

interface HabitsRepository {
    suspend fun getHabits(): List<Habit>
}