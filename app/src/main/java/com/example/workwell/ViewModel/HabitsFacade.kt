package com.example.workwell.ViewModel

import Habit


interface HabitsFacade {
    suspend fun getHabits(): List<Habit>

}