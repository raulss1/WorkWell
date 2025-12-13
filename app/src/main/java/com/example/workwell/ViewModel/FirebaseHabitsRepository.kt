package com.example.workwell.ViewModel

import Habit

class FirebaseHabitsRepository(
    private val habitFacade: HabitsFacade
) : HabitsRepository {
    override suspend fun getHabits(): List<Habit> = habitFacade.getHabits()
}