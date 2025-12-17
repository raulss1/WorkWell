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
}
