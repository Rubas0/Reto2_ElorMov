package com.example.reto2_elormov.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reto2_elormov.data.dto.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/*
    Lógica de negocio: maneja estados (Loading, Success, Error).
 */
class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            try {
                val response = repository.login(username, password)
                Log.d("LoginVM", "HTTP ${response.code()} ${response.message()}")
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("LoginVM", "Resp success=${body?.success} msg=${body?.message} userId=${body?.user?.id}")
                    if (body?.success == true && body.user?.id != null) {
                        _state.value = LoginState.Success(
                            username = username,
                            userId = body.user.id //  Pasamos también userId
                        )
                    } else {
                        _state.value = LoginState.Error(body?.message ?: "Credenciales incorrectas")
                    }
                } else {
                    _state.value = LoginState.Error("HTTP ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                _state.value = LoginState.Error("Error de conexión: ${e.message ?: "desconocido"}")
            }
        }
    }
}