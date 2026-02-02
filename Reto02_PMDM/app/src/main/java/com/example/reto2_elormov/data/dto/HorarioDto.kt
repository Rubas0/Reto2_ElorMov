package com.example.reto2_elormov.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTOs relacionados con el horario semanal de profesores y alumnos. Incluyen detalles sobre las clases,
 */
data class HorarioDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("dia") val dia: String,
    @SerializedName("hora") val hora: Int,
    @SerializedName("tipo") val tipo: String, // CLASE, TUTORIA, GUARDIA, VACIO
    @SerializedName("asignatura") val asignatura: String? = null,
    @SerializedName("curso") val curso: String? = null,
    @SerializedName("ciclo") val ciclo: String? = null,
    @SerializedName("aula") val aula: String? = null,
    @SerializedName("reunion") val reunion: ReunionDto? = null
)

data class HorarioSemanalDto(
    @SerializedName("usuario_id") val usuarioId: Int,
    @SerializedName("tipo_usuario") val tipoUsuario: String, // PROFESOR o ALUMNO
    @SerializedName("horario") val horario: List<HorarioDto>
)

data class ReunionDto(
    @SerializedName("id") val id: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("tema") val tema: String,
    @SerializedName("dia") val dia: String,
    @SerializedName("hora") val hora: Int,
    @SerializedName("aula") val aula: String,
    @SerializedName("estado") val estado: String, // PENDIENTE, CONFLICTO, ACEPTADA, CANCELADA
    @SerializedName("ubicacion") val ubicacion: String? = null,
    @SerializedName("codigo_centro") val codigoCentro: String? = "15112"
)