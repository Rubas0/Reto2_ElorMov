package com.example.reto2_elormov.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reto2_elormov.data.dto.HorarioSemanalDto
import com.example.reto2_elormov.data.dto.repository.HorarioRepository
import com.example.reto2_elormov.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val horarioRepository = HorarioRepository(RetrofitClient.elorServApi)

    private val _horario = MutableStateFlow<Result<HorarioSemanalDto>?>(null)
    val horario: StateFlow<Result<HorarioSemanalDto>?> = _horario

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
        Carga el horario del usuario (profesor o alumno) para la semana especificada.
     */
    fun cargarHorario(usuarioId: Int, esProfesor: Boolean, semana: Int? = null) {
        viewModelScope.launch {
            _isLoading.value = true // Iniciar carga

            val result = if (esProfesor) {
                horarioRepository.getHorarioProfesor(usuarioId, semana)
            } else {
                horarioRepository.getHorarioAlumno(usuarioId, semana)
            }

            _horario.value = result
            _isLoading.value = false // Fin de carga
        }
    }

    /**
     * Limpia el estado del horario
     */
    fun limpiarHorario() {
        _horario.value = null
    }
}
