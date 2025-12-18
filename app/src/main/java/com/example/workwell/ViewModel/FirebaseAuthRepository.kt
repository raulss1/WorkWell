package com.example.workwell.ViewModel

sealed class AuthResult {
    data class Success(val userId: String? = null) : AuthResult()
    data class Error(val code: String?, val message: String) : AuthResult()
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

    override suspend fun sendPasswordResetEmail(email: String): AuthResult =
        authFacade.sendPasswordResetEmail(email)

    override suspend fun updatePassword(email: String, currentPass: String, newPassword: String): AuthResult =
        authFacade.updatePassword(email, currentPass, newPassword)
}