package com.example.workwell.Model

import android.app.Notification

interface UserProvider {
    suspend fun getUser(id: String): User
    suspend fun getUserTask(id: String): List<Task>
    suspend fun createUserTask(userId: String, task: String, startDate: java.util.Date, endDate: java.util.Date, type: String, priority: String, eatNotificationTime: String, standNotificationTime: String, strechNotificationTime: String)
    fun editUser(id: String): User
}