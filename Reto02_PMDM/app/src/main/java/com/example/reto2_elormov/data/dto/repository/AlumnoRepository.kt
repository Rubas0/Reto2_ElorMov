package com.example.reto2_elormov.data.dto.repository

import com.example.reto2_elormov.data.api.ElorServApi
import com.example.reto2_elormov.data.dto.AlumnoDTO
import com.example.reto2_elormov.data.dto.AlumnosResponseDTO
import retrofit2.Response

class AlumnoRepository(private val api: ElorServApi) {

    suspend fun getAlumnos(): Response<AlumnosResponseDTO> { // import response de retrofit
        return api.getAlumnos()
    }

    suspend fun getAlumnoById(alumnoId: Int): Response<AlumnoDTO> {
        return api.getAlumnoById(alumnoId)
    }
}