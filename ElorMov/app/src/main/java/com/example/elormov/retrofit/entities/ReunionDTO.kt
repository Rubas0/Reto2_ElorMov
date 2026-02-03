package com.example.elormov.retrofit.entities

data class ReunionDTO(
    val id: Int,
    val estado: String,
    val estadoEus: String,
    val titulo: String,
    val asunto: String,
    val dia: Int,
    val semana: Int,
    val hora: Int,
    val aula: String,
    val idCentro: String,
    val profesor: UserDTO,
    val alumno: UserDTO
)
