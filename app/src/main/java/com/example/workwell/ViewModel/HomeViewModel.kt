package com.example.workwell.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workwell.Model.Date
import com.example.workwell.Model.Task
import com.example.workwell.Model.UserProviderFirebase
import com.google.firebase.auth.FirebaseAuth
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

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.let { user ->
            loadUserData(user.uid)
        } ?: run {
            _userName.value = "Error: Sesión no encontrada"
        }

        currentUser?.let { user ->
            loadUserTask(user.uid)
        }
    }

    fun loadUserData(userId: String) {
        viewModelScope.launch {
            try {
                val user = userProvider.getUser(userId)

                _userName.value = user.userName
                _birthday.value = user.birthday
                _email.value = user.email
                _name.value = user.name

            } catch (e: Exception) {
                _userName.value = "Error al cargar"
            }
        }
    }

    fun loadUserTask(userId: String) {
        viewModelScope.launch {
            try {
                val task = userProvider.getUserTask(userId)

                _tasks.value = task

            } catch (e: Exception) {
                _tasks.value = emptyList()
            }
        }
    }
}