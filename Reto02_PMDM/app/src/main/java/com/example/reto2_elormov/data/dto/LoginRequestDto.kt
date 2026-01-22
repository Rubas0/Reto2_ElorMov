package com.example.reto2_elormov.data.dto

data class LoginRequestDTO(
    val username: String,
    val password: String // Cifrado RSA antes de crear este objeto
)