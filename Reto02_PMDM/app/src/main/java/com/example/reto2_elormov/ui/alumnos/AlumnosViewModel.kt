package com.example.reto2_elormov.ui.alumnos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reto2_elormov.data.dto.AlumnoDTO
import com.example.reto2_elormov.data.dto.repository.AlumnoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AlumnosState {
    object Idle : AlumnosState()
    object Loading : AlumnosState()
    data class Success(val alumnos: List<AlumnoDTO>) : AlumnosState()
    data class Error(val message: String) : AlumnosState()
}

/**
 *
 */
class AlumnosViewModel(private val repository: AlumnoRepository) : ViewModel() {

    private val _state = MutableStateFlow<AlumnosState>(AlumnosState.Idle)
    val state: StateFlow<AlumnosState> = _state

    fun loadAlumnos() {
        viewModelScope.launch {
            _state.value = AlumnosState.Loading
            try {
                val response = repository.getAlumnos()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.alumnos != null) {
                        _state.value = AlumnosState.Success(body.alumnos)
                    } else {
                        _state.value =
                            AlumnosState.Error(body?.message ?: "No se encontraron alumnos")
                    }
                } else {
                    _state.value = AlumnosState.Error("HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = AlumnosState.Error("Error: ${e.message}")
            }
        }
    }
}