package com.example.elormov.retrofit.entities

data class LoginResponseDTO(
    var success: Boolean,
    var message: String,
    var user: UserDTO
)
