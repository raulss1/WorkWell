package com.example.workwell.ViewModel

import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

sealed class AuthResult {
    data class Success(val userId: String): AuthResult()
    data class Error(val code: String?, val message: String): AuthResult()
}


class AuthRepository (
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun login(email: String, password: String): AuthResult {
        val login = auth.signInWithEmailAndPassword(email,password).await()
        Log.d("user_id", "Usuario creado con UID: ${login.user?.uid}")
        val uid = login.user?.uid
        if (uid != null) {
            return AuthResult.Success(uid)
        } else {
            return AuthResult.Error("NO_UID", "No se obtuvo UID tras el registro")
        }
    }

    suspend fun userNameExists(username: String): Boolean {
        val userNameExist = db.collection("user")
            .whereEqualTo("UserName", username)
            .get()
            .await()
        if (!userNameExist.isEmpty) {
            return true
        }
        Log.d("pepe", "userNameExist: $userNameExist")
        return false
    }

    suspend fun emailExists(email: String): Boolean {
        val userEmailExist = db.collection("user")
            .whereEqualTo("Email", email)
            .get()
            .await()
        if (!userEmailExist.isEmpty) {
            return true
        }
        Log.d("pepe", "userEmailExist: $userEmailExist")
        return false
    }

    suspend fun registerUser(email: String, password: String): AuthResult {
        val user = auth.createUserWithEmailAndPassword(email, password).await()
        Log.d("user_id", "Usuario creado con UID: ${user.user?.uid}")

        val uid = user.user?.uid
        if (uid != null) {
            return AuthResult.Success(uid)
        } else {
            return AuthResult.Error("NO_UID", "No se obtuvo UID tras el registro")
        }
    }

    suspend fun saveUserData(name: String, username: String, email: String, date: java.util.Date) {
        val userId = auth.currentUser!!.uid
        Log.d("user_id", "Usuario creado con UID: $userId")

        // CORRECCIÓN DE SEGURIDAD: Eliminada la contraseña
        val userData = hashMapOf(
            "Name" to name,
            "UserName" to username,
            "Email" to email,
            "BirthDate" to Timestamp(date),
            "UserId" to userId
        )

        db.collection("user").document(userId).set(userData).await()
    }

}