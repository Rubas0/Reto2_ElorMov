package com.example.reto2_elormov.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elormov.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val username: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(private val repo: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state

    fun login(username: String, password: String) {
        _state.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val resp = repo.login(username, password)
                if (resp.isSuccessful) {
                    val body = resp.body()
                    if (body?.success == true && body.user != null) {
                        _state.value = LoginState.Success(body.user.username ?: username)
                    } else {
                        _state.value = LoginState.Error(body?.message ?: "Credenciales inválidas")
                    }
                } else {
                    // 401, 500, etc.
                    _state.value = LoginState.Error("Error ${resp.code()}: ${resp.message()}")
                }
            } catch (e: Exception) {
                _state.value = LoginState.Error("Sin conexión o servidor no disponible")
            }
        }
    }
}