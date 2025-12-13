package com.example.workwell.ViewModel

interface AuthFacade {
    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(email: String, password: String): AuthResult
    fun logout()
    fun getCurrentUserId(): String?
}