package com.example.workwell.ViewModel

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

sealed class AuthResult {
    data class Success(val userId: String): AuthResult()
    data class Error(val code: String?, val message: String): AuthResult()
}

//no conoce firebase, solo decide el flujo
//conoce auth y firestore
class FirebaseAuthRepository (
    private val authFacade: AuthFacade,
    private val userFacade: UserFacade,
) : AuthRepository {


    override suspend fun login(email: String, password: String): AuthResult {
        return authFacade.login(email, password)
    }

    override suspend fun userNameExists(username: String): Boolean {
        return userFacade.usernameExists(username)
    }

    override suspend fun emailExists(email: String): Boolean {
        return userFacade.emailExists(email)
    }

    override suspend fun register(
        name: String,
        username: String,
        email: String,
        password: String,
        birthDate: java.util.Date
    ): AuthResult {
        val authResult = authFacade.register(email, password)
        if (authResult is AuthResult.Success) {
            try {
                userFacade.saveUser(authResult.userId, name, username, email, birthDate)
            } catch (e: Exception) {
                authFacade.logout()
                return AuthResult.Error("SAVE_FAILED", e.message ?: "Error guardando datos")
            }
        }
        return authResult

    }

    override fun getCurrentUserId(): String? {
        return authFacade.getCurrentUserId()
    }

    override fun logout() {
        authFacade.logout()
    }
}