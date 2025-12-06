package com.example.workwell.Model

interface UserProvider {
    suspend fun getUser(id: String): User
    suspend fun getUserTask(id: String): List<Task>
    suspend fun createUserTask(userId: String, task: String)
    fun editUser(id: String): User
}