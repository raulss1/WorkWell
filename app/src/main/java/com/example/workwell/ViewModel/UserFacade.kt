package com.example.workwell.ViewModel

interface UserFacade {
    suspend fun usernameExists(username: String): Boolean
    suspend fun emailExists(email: String): Boolean
    suspend fun saveUser(
        userId: String,
        name: String,
        username: String,
        email: String,
        birthDate: java.util.Date
    )
}