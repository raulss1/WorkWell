package com.example.workwell.Model

interface UserProvider {
    suspend fun getUser(id: String): User
    fun createUser(): User
    fun editUser(id: String): User
}