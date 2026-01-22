package com.example.reto2_elormov.data.dto

data class LoginResponseDTO(
    val success: Boolean,
    val message: String?,
    val user: UserDTO?
)