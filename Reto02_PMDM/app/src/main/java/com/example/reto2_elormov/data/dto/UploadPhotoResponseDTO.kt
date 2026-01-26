package com.example.reto2_elormov.data.dto

// DTO para la respuesta de la subida de foto
data class UploadPhotoResponseDTO(
    val success: Boolean,
    val message: String?,
    val photoUrl: String?
)