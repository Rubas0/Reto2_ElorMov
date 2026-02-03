package com.example.elormov.retrofit.entities

data class HorarioDTO(
    val id: Int,
    val dia: String,
    val hora: Int,
    val aula: String,
    val observaciones: String,
    val profe: UserDTO,
    val modulo: ModuloDTO
)
