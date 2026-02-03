package com.example.elormov.retrofit.entities

import java.time.LocalDate

data class MatriculacionDTO(
    val id: Int,
    val curso: Int,
    val fecha: LocalDate,
    val alumno: UserDTO,
    val ciclo: CicloDTO
)
