package com.example.workwell.Model

import android.app.Notification

interface UserProvider {
    suspend fun getUser(id: String): User
    suspend fun getUserTask(id: String): List<Task>
    suspend fun createUserTask(userId: String, task: String, startDate: Date, endDate: Date, type: String, priority: String, notificationTime: String)
    fun editUser(id: String): User
}