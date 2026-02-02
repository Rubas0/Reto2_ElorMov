package com.example.reto2_elormov.data.dto

data class AlumnoDTO(
    val id: Int?,
    val username: String?,
    val nombre: String?,
    val apellidos: String?,
    val email: String?,
    val ciclo: String?,      // DAM, DAW, ASIR
    val curso: String?,      // 1º, 2º
    val dualIntensiva: Boolean?,
    val argazkiaUrl: String?
)

data class AlumnosResponseDTO(
    val success: Boolean,
    val message: String?,
    val alumnos: List<AlumnoDTO>?
)