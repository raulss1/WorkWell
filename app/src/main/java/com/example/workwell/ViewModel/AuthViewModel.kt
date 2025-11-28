package com.example.workwell.ViewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale

class AuthViewModel : ViewModel() {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    private val _authState = MutableLiveData<AuthState>()
    val authState : LiveData<AuthState> = _authState

    val name = mutableStateOf("")
    val username = mutableStateOf("")
    val email = mutableStateOf("")
    val passwd = mutableStateOf("")
    val confirmPasswd = mutableStateOf("")
    val birthDate = mutableStateOf("")

    init {
        checkAuthentication()
    }

    fun checkAuthentication() {
        if(auth.currentUser==null){
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated

        }
    }

    fun login(email: String, password: String) {
        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Login failed")
                }
            }
    }

    fun signup() {
        // 1. Validaciones
        if(name.value.isEmpty() || username.value.isEmpty() || email.value.isEmpty() ||
            passwd.value.isEmpty() || confirmPasswd.value.isEmpty() || birthDate.value.isEmpty()){
            _authState.value = AuthState.Error("Todos los campos deben estar completos")
            return
        }

        if(passwd.value != confirmPasswd.value){
            _authState.value = AuthState.Error("Las contraseñas no coinciden")
            return
        }

        // Manejo de errores de fecha try-catch por si el formato falla
        val date = try {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(birthDate.value)
        } catch (e: Exception) {
            null
        }

        if(date == null){
            _authState.value = AuthState.Error("Fecha inválida")
            return
        }

        // 2. Estado Cargando
        _authState.value = AuthState.Loading

        // 3. Crear usuario en Auth
        auth.createUserWithEmailAndPassword(email.value, passwd.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser!!.uid
                    Log.d("user_id", "Usuario creado con UID: $userId")

                    // CORRECCIÓN DE SEGURIDAD: Eliminada la contraseña
                    val userData = hashMapOf(
                        "Name" to name.value,
                        "UserName" to username.value,
                        "Email" to email.value,
                        "BirthDate" to com.google.firebase.Timestamp(date),
                        "UserId" to userId // Opcional: útil tener el ID dentro del documento
                    )

                    // 4. Guardar datos adicionales en Firestore
                    db.collection("user").document(userId)
                        .set(userData)
                        .addOnSuccessListener {
                            // AQUÍ es el único momento seguro para cambiar el estado
                            _authState.value = AuthState.Authenticated
                        }
                        .addOnFailureListener { e ->
                            // Si falla Firestore, podrías considerar borrar el usuario de Auth para no dejar "usuarios zombis"
                            _authState.value = AuthState.Error(e.message ?: "Error guardando datos")
                        }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Error al registrar")
                }
            }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()

}