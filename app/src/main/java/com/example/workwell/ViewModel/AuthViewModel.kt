package com.example.workwell.ViewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

interface registerUser {
    val name: MutableState<String>
    val username: MutableState<String>
    val email: MutableState<String>
    val birthDate: MutableState<String>
    val passwd: MutableState<String>
    val confirmPasswd: MutableState<String>
}

class firebaseUser: registerUser {
    override val name = mutableStateOf("")
    override val username = mutableStateOf("")
    override val email = mutableStateOf("")
    override val birthDate = mutableStateOf("")
    override val passwd = mutableStateOf("")
    override val confirmPasswd = mutableStateOf("")
}

class AuthViewModel : ViewModel() {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    private val _authState = MutableLiveData<AuthState>()
    val authState : LiveData<AuthState> = _authState
    val user: firebaseUser = firebaseUser()
    val usernameError = mutableStateOf("")
    val emailError = mutableStateOf("")
    val confirmPasswdError = mutableStateOf("")
    val birthDateError = mutableStateOf("")

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

    fun resetErrorFields() {
        usernameError.value = ""
        emailError.value = ""
        confirmPasswdError.value = ""
        birthDateError.value = ""
    }

    fun validateFieldsLocal(): Boolean {
        var hasError = false

        // 1. Validaciones
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(user.email.value).matches()) {
            emailError.value = "El formato del correo no es válido"
            hasError = true
        }

        if(user.passwd.value != user.confirmPasswd.value){
            confirmPasswdError.value =  "Las contraseñas no coinciden"
            hasError = true
        }

        if(user.passwd.value.length < 6){
            confirmPasswdError.value =  "La contraseña debe tener al menos 6 caracteres"
            hasError = true
        }
        return hasError
    }

    private suspend fun checkUserNameExist(): Boolean {
        val userNameExist = db.collection("user")
            .whereEqualTo("UserName", user.username.value)
            .get()
            .await()
        if (!userNameExist.isEmpty) {
            usernameError.value = "El nombre de usuario ya está en uso"
            return true
        }
        Log.d("pepe", "userNameExist: $userNameExist")
        return false
    }

    private suspend fun checkEmailExist(): Boolean {
        val userEmailExist = db.collection("user")
            .whereEqualTo("Email", user.email.value)
            .get()
            .await()
        if (!userEmailExist.isEmpty) {
            emailError.value = "El email ya está en uso"
            return true
        }
        Log.d("pepe", "userEmailExist: $userEmailExist")
        return false
    }
    fun signup() {
        resetErrorFields()

        // 2. Estado Cargando
        _authState.value = AuthState.Loading

        val fields = listOf(user.name, user.username, user.email, user.birthDate, user.passwd, user.confirmPasswd)
        val isAnyFieldEmpty = fields.any { it.value.isEmpty() }
        if (isAnyFieldEmpty) {
            _authState.value = AuthState.Error("Todos los campos deben estar completos")
            return
        }
        viewModelScope.launch {
            var hasLocalError = validateFieldsLocal()
            var isEmailTaken = checkEmailExist()
            var isUserNameTaken = checkUserNameExist()

            if (!(hasLocalError || isUserNameTaken || isEmailTaken)){
                var date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .parse(user.birthDate.value)
                registerUser(date)
            }
        }
    }

    private fun registerUser(date: java.util.Date) {
        // 3. Crear usuario en Auth
        auth.createUserWithEmailAndPassword(user.email.value, user.passwd.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserData(date)
                } else {
                    _authState.value = AuthState.Error("Error al registrar, inténtelo más tarde")
                    _authState.value = AuthState.Unauthenticated
                }
            }
    }

    fun saveUserData(date: java.util.Date) {
        val userId = auth.currentUser!!.uid
        Log.d("user_id", "Usuario creado con UID: $userId")

        // CORRECCIÓN DE SEGURIDAD: Eliminada la contraseña
        val userData = hashMapOf(
            "Name" to user.name.value,
            "UserName" to user.username.value,
            "Email" to user.email.value,
            "BirthDate" to Timestamp(date),
            "UserId" to userId
        )

        // 4. Guardar datos adicionales en Firestore
        db.collection("user").document(userId)
            .set(userData)
            .addOnSuccessListener {
                // AQUÍ es el único momento seguro para cambiar el estado
                _authState.value = AuthState.Authenticated
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(e.message ?: "Error guardando datos")
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