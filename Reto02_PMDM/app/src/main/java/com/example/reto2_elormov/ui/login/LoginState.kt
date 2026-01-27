package com.example.reto2_elormov.ui.login

/*
Sealed class: funciona como una clase abstracta pero con un conjunto limitado de subclases.
Permite representar diferentes estados de la UI de manera segura y controlada.
 */
sealed class LoginState {
    object Idle : LoginState() // Estado inicial, sin acción
    object Loading : LoginState() // // Enviando credenciales al servidor
    data class Success(val username: String, val userId: Int) : LoginState() // Login correcto
    data class Error(val message: String) : LoginState()  // Login incorrecto o error de red
}