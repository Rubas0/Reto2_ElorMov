package com.example.reto2_elormov.data.dto

data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val user: UserDto?
)