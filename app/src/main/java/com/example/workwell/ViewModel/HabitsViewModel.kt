package com.example.workwell.ViewModel

import Habit
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class HabitsViewModel(
    private val repository: HabitsRepository = FirebaseHabitsRepository(
        FirestoreHabitFacade()
    )
) : ViewModel() {

    sealed class HabitsUiState {
        object Loading : HabitsUiState()
        data class Success(val habits: List<Habit>) : HabitsUiState()
        data class Error(val message: String) : HabitsUiState()
    }

    private val _state = MutableLiveData<HabitsUiState>(HabitsUiState.Loading)
    val state: LiveData<HabitsUiState> = _state

    init {
        loadHabits()
    }

    fun loadHabits() {
        if (_state.value != HabitsUiState.Loading) {
            _state.value = HabitsUiState.Loading
        }

        viewModelScope.launch {
            try {
                val habits = repository.getHabits()
                _state.value = HabitsUiState.Success(habits)
            } catch (e: Exception) {
                _state.value = HabitsUiState.Error(e.message ?: "Error al cargar hábitos.")
            }
        }
    }
}