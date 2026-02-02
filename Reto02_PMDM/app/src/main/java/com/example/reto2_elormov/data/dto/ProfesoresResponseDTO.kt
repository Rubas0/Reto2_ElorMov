package com.example.reto2_elormov.data.dto

data class ProfesoresResponseDTO (
        val success: Boolean,
        val message: String?,
        val profesores: List<ProfesorDTO>?
    )