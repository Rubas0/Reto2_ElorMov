package com.example.reto2_elormov.ui.profesores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reto2_elormov.data.dto.ProfesorDTO
import com.example.reto2_elormov.data.dto.repository.ProfesorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class ProfesoresState {
    object Idle : ProfesoresState()
    object Loading : ProfesoresState()
    data class Success(val profesores: List<ProfesorDTO>) : ProfesoresState()
    data class Error(val message: String) : ProfesoresState()
}

class ProfesoresViewModel(private val repository: ProfesorRepository) : ViewModel() {

    private val _state = MutableStateFlow<ProfesoresState>(ProfesoresState.Idle)
    val state: StateFlow<ProfesoresState> = _state

    fun loadProfesores() {
        viewModelScope.launch {
            _state.value = ProfesoresState.Loading
            try {
                val response = repository.getProfesores()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.profesores != null) {
                        _state.value = ProfesoresState.Success(body.profesores)
                    } else {
                        _state.value = ProfesoresState.Error(body?.message ?: "No se encontraron profesores")
                    }
                } else {
                    _state.value = ProfesoresState.Error("HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = ProfesoresState.Error("Error: ${e.message}")
            }
        }
    }
}