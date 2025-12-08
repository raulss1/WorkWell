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

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {
    private val _authState = MutableLiveData<AuthState>()
    val authState : LiveData<AuthState> = _authState
    val usernameError = mutableStateOf("")
    val emailError = mutableStateOf("")
    val passwordError = mutableStateOf("")
    val confirmPasswdError = mutableStateOf("")
    val birthDateError = mutableStateOf("")

    fun login(email: String, password: String) {
        resetErrorFields()
        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Hay que rellenar todos los campos")
            return
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError.value = "El formato del correo no es válido"
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            when (val result = repository.login(email, password)) {
                is AuthResult.Success -> {
                    _authState.value = AuthState.Authenticated
                }

                is AuthResult.Error -> {
                    when (result.code) {
                        "ERROR_USER_NOT_FOUND",
                        "ERROR_INVALID_CREDENTIAL",
                        "ERROR_WRONG_PASSWORD" -> {
                            _authState.value = AuthState.Error("Correo o contraseña incorrectos")
                        }

                        else -> {
                            _authState.value = AuthState.Error("Ha ocurrido un error, inténtelo de nuevo")
                        }
                    }
                }
            }
        }
    }

    fun resetErrorFields() {
        usernameError.value = ""
        emailError.value = ""
        confirmPasswdError.value = ""
        birthDateError.value = ""
        _authState.value = AuthState.Unauthenticated
    }

    fun validateLocalFields(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var hasError = false

        // 1. Validaciones
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError.value = "El formato del correo no es válido"
            hasError = true
        }

        if(password != confirmPassword){
            confirmPasswdError.value =  "Las contraseñas no coinciden"
            hasError = true
        }

        if(password.length < 6){
            confirmPasswdError.value =  "La contraseña debe tener al menos 6 caracteres"
            hasError = true
        }
        return hasError
    }

    fun signup(
        name: String,
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        birthDate: String
    ) {
        resetErrorFields()

        val fields = listOf(name, username, email, birthDate, password, confirmPassword)
        val isAnyFieldEmpty = fields.any { it.isEmpty() }
        if (isAnyFieldEmpty) {
            _authState.value = AuthState.Error("Todos los campos deben estar completos")
            return
        }

        viewModelScope.launch {
            val hasLocalError = validateLocalFields(email, password, confirmPassword)
            val isUserNameTaken = repository.userNameExists(username)
            if (isUserNameTaken) {
                usernameError.value = "El nombre de usuario ya está en uso"
            }
            val isEmailTaken = repository.emailExists(email)
            if (isEmailTaken) {
                emailError.value = "El email ya está en uso"
            }
            if (!(hasLocalError || isUserNameTaken || isEmailTaken)){
                _authState.value = AuthState.Loading
                val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(birthDate)

                try {
                    val result = repository.registerUser(email, password)

                    if (result is AuthResult.Success) {
                        try {
                            repository.saveUserData(name, username, email, date)
                            _authState.value = AuthState.Authenticated

                        } catch (e: Exception) {
                            //usuarios fantasmas
                            repository.getAuth().currentUser?.delete()?.await()
                            _authState.value = AuthState.Error("Error guardando datos. Intentalo nuevamente.")
                        }
                    } else {
                        _authState.value = AuthState.Error("Error al registrar usuario")
                    }

                } catch (e: Exception) {
                    _authState.value = AuthState.Error(e.message ?: "Error desconocido")
                }

            }
        }
    }

    fun signout() {
        repository.getAuth().signOut()
        _authState.value = AuthState.Unauthenticated
    }

}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()

}