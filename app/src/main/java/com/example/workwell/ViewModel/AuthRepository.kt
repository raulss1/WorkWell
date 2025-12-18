package com.example.workwell.ViewModel

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(
        name: String,
        username: String,
        email: String,
        password: String,
        birthDate: java.util.Date
    ): AuthResult

    suspend fun userNameExists(username: String): Boolean
    suspend fun emailExists(email: String): Boolean

    fun logout()

    fun getCurrentUserId(): String?

    suspend fun sendPasswordResetEmail(email: String): AuthResult

    suspend fun updatePassword(email: String, currentPass: String, newPassword: String): AuthResult
}