package com.example.workwell.ViewModel

import com.google.firebase.auth.FirebaseAuth

interface AuthRepository {
    fun getAuth(): FirebaseAuth
    suspend fun login(email: String, password: String): AuthResult
    suspend fun userNameExists(username: String): Boolean
    suspend fun emailExists(email: String): Boolean

    suspend fun registerUser(email: String, password: String): AuthResult

    suspend fun saveUserData(name: String, username: String, email: String, date: java.util.Date)
}