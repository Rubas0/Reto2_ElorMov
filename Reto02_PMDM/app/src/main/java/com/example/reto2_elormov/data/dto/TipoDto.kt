package com.example.reto2_elormov.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Clase TipoDTO que representa un tipo con su id y nombres en diferentes idiomas. Mapea los campos JSON
 * utilizando la anotación @SerializedName para asegurar la correcta deserialización.
 */
data class TipoDTO (
    @SerializedName("id") val id: Int,
    @SerializedName("name") val nombre: String? = null,
    val nameEu: String?
)