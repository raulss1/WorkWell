package com.example.workwell.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workwell.Model.UserProviderFirebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val userProvider: UserProviderFirebase) : ViewModel() {

    private val _userName = MutableStateFlow("Cargando...")
    val userName: StateFlow<String> = _userName

    private val _birthday = MutableStateFlow("Cargando...")
    val birthday: StateFlow<String> = _birthday

    private val _email = MutableStateFlow("Cargando...")
    val email: StateFlow<String> = _email

    private val _name = MutableStateFlow("Cargando...")
    val name: StateFlow<String> = _name

    init {
        loadUserData("clyFkPuVKUOoNSEA3jnZ")
    }

    private fun loadUserData(userId: String) {
        viewModelScope.launch {
            try {
                val user = userProvider.getUser(userId)

                _userName.value = user.userName
                _birthday.value = user.birthday
                _email.value = user.email
                _name.value = user.name

            } catch (e: Exception) {
                // Manejo de errores
                _userName.value = "Error al cargar"
                // Puedes usar otro StateFlow para mensajes de error
            }
        }
    }
}