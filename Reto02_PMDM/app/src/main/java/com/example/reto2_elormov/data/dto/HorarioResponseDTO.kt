package com.example.reto2_elormov.data.dto

import com.google.gson.annotations.SerializedName

class HorarioResponseDTO (
        @SerializedName("success") val success: Boolean = false,
        @SerializedName("message") val message: String? = null,
        @SerializedName("horario") val horario: List<HorarioDto>? = null
)