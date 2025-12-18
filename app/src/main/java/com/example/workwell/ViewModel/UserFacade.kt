package com.example.workwell.ViewModel

import java.util.Date

interface UserFacade {
    suspend fun usernameExists(username: String): Boolean
    suspend fun emailExists(email: String): Boolean
    suspend fun saveUser(
        userId: String?,
        name: String,
        username: String,
        email: String,
        birthDate: Date
    )
}