package com.example.reto2_elormov.data.dto.repository

import com.example.reto2_elormov.data.api.ElorServApi
import com.example.reto2_elormov.data.dto.HorarioResponseDTO
import com.example.reto2_elormov.data.dto.ProfesoresResponseDTO
import retrofit2.Response


class ProfesorRepository(private val api: ElorServApi) {

    suspend fun getProfesores(): Response<ProfesoresResponseDTO> {
        return api.getProfesores()
    }

    suspend fun getHorarioProfesor(profesorId: Int): Response<HorarioResponseDTO> {
        return api.getHorarioProfesor(profesorId)
    }
}