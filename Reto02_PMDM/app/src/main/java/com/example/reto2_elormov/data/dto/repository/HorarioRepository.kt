package com.example.reto2_elormov.data.dto.repository

import com.example.reto2_elormov.data.api.ElorServApi
import com.example.reto2_elormov.data.dto.HorarioSemanalDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
/**
 * Repositorio para gestionar las operaciones relacionadas con horarios
 */
class HorarioRepository(
    private val api: ElorServApi
) {

    /**
     * - Obtiene el horario de un profesor desde el servidor
     * @param profesorId ID del profesor
     * @param semana Número de semana
     * @return Result<HorarioSemanalDto> con el horario o un error
     */
    suspend fun getHorarioProfesor(
        profesorId: Int,
        semana: Int? = null
    ): Result<HorarioSemanalDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.getHorarioProfesor(profesorId, semana)

            when {
                response.isSuccessful && response.body() != null -> {
                    Result.success(response.body()!!)
                }
                response.code() == 404 -> {
                    Result.failure(Exception("Horario no existe para este profesor"))
                }
                response.code() == 500 -> {
                    Result.failure(Exception("Error en el servidor"))
                }
                else -> {
                    Result.failure(Exception("Error al obtener horario: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.localizedMessage ?: "Sin conexión"}"))
        }
    }

    /**
     * - Obtiene el horario de un alumno desde el servidor
     * El servidor genera dinámicamente el horario a partir de los horarios de profesores
     * @param alumnoId ID del alumno
     * @param semana Número de semana (opcional, por defecto semana actual)
     * @return Result<HorarioSemanalDto> con el horario o un error
     */
    suspend fun getHorarioAlumno(
        alumnoId: Int,
        semana: Int? = null
    ): Result<HorarioSemanalDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.getHorarioAlumno(alumnoId, semana)

            when {
                response.isSuccessful && response.body() != null -> {
                    Result.success(response.body()!!)
                }
                response.code() == 404 -> {
                    Result.failure(Exception("Horario no existe para este alumno"))
                }
                response.code() == 500 -> {
                    Result.failure(Exception("Error en el servidor al generar horario"))
                }
                else -> {
                    Result.failure(Exception("Error al obtener horario: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.localizedMessage ?: "Sin conexión"}"))
        }
    }
}