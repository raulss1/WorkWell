package com.example.workwell.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class AuthViewModel(
    private val repository: AuthRepository = FirebaseAuthRepository(
        FirebaseAuthFacade(),
        FirestoreUserFacade()
    )
) : ViewModel() {
    private val _authState = MutableLiveData<AuthState>()
    val authState : LiveData<AuthState> = _authState

    val usernameError = mutableStateOf("")
    val emailError = mutableStateOf("")
    val passwordError = mutableStateOf("")
    val confirmPasswdError = mutableStateOf("")
    val birthDateError = mutableStateOf("")

    init {
        checkSession()
    }

    private fun checkSession() {
        val uid = repository.getCurrentUserId()
        _authState.value =
            if (uid != null) AuthState.Authenticated
            else AuthState.Unauthenticated
    }

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

                when (
                    val result = repository.register(
                        name, username, email, password, date
                    )
                ) {
                    is AuthResult.Success -> {
                        _authState.value = AuthState.Authenticated
                    }
                    is AuthResult.Error -> {
                        _authState.value = AuthState.Error("Error al registrar usuario")
                    }

                }
            }
        }
    }

    fun signout() {
        repository.logout()
        _authState.value = AuthState.Unauthenticated
    }

    fun resetPassword(email: String) {
        if (email.isEmpty()) {
            emailError.value = "Introduce un correo"
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            when (val result = repository.sendPasswordResetEmail(email)) {
                is AuthResult.Success -> {
                    _authState.value =
                        AuthState.Error("Te hemos enviado un correo para restablecer la contraseña")
                }

                is AuthResult.Error -> {
                    _authState.value =
                        AuthState.Error(result.message)
                }
            }
        }
    }

    fun changePassword(newPassword: String) {
        if (newPassword.length < 6) {
            passwordError.value = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            when (val result = repository.updatePassword(newPassword)) {
                is AuthResult.Success -> {
                    _authState.value =
                        AuthState.Error("Contraseña actualizada correctamente")
                }

                is AuthResult.Error -> {
                    when (result.code) {
                        "ERROR_REQUIRES_RECENT_LOGIN" ->
                            _authState.value =
                                AuthState.Error("Por seguridad, vuelve a iniciar sesión")

                        else ->
                            _authState.value =
                                AuthState.Error(result.message)
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
        passwordError.value = ""
        _authState.value = AuthState.Error("")
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
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()

}