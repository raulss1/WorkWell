package com.example.workwell.ViewModel

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.tasks.await

//maneja la logica con auth
class FirebaseAuthFacade(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

) : AuthFacade {
    override suspend fun login(email: String, password: String): AuthResult =
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.getIdToken(true)?.await()
            val uid = result.user?.uid
            uid?.let { AuthResult.Success(it) }
                ?: AuthResult.Error("NO_UID", "No se obtuvo UID")
        } catch (e: Exception) {
            val code = (e as? FirebaseAuthException)?.errorCode
            AuthResult.Error(code, e.message ?: "Error desconocido")
        }

    override suspend fun register(email: String, password: String): AuthResult =
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid
            uid?.let { AuthResult.Success(it) }
                ?: AuthResult.Error("NO_UID", "No se obtuvo UID")
        } catch (e: Exception) {
            val code = (e as? FirebaseAuthException)?.errorCode
            AuthResult.Error(code, e.message ?: "Error desconocido")
        }

    override fun logout() {
        auth.signOut()
    }

    override fun getCurrentUserId(): String? = auth.currentUser?.uid

    override suspend fun sendPasswordResetEmail(email: String): AuthResult =
        try {
            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success(null)
        } catch (e: Exception) {
            val code = (e as? FirebaseAuthException)?.errorCode
            AuthResult.Error(code, e.message ?: "Error desconocido")
        }

    override suspend fun updatePassword(email: String, currentPass: String, newPassword: String): AuthResult =
        try {
            val user = auth.currentUser
            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, currentPass)

            user?.reauthenticate(credential)?.await()
            user?.updatePassword(newPassword)?.await()
            user?.getIdToken(true)?.await()

            AuthResult.Success(user?.uid)
        } catch (e: Exception) {
            val code = (e as? FirebaseAuthException)?.errorCode
            AuthResult.Error(code, e.message ?: "Error en el proceso de cambio")
        }
}
