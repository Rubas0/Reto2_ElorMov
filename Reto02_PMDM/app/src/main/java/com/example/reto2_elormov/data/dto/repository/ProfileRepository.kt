package com.example.reto2_elormov.data.dto.repository

import com.example.reto2_elormov.data.api.ElorServApi
import com.example.reto2_elormov.data.dto.UploadPhotoResponseDTO
import com.example.reto2_elormov.data.dto.UserDTO
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File

/*
Capa intermedia que llama a la API. Realiza operaciones relacionadas con el perfil de usuario.
 */
class ProfileRepository(private val api: ElorServApi) {

    suspend fun getProfile(userId: Int): Response<UserDTO> {
        return api.getProfile(userId)
    }

    suspend fun uploadPhoto(userId: Int, file: File): Response<UploadPhotoResponseDTO> {
        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull()) // Ajusta el tipo MIME según el tipo de imagen, es decir, image/png si es PNG
        val photoPart = MultipartBody.Part.createFormData("photo", file.name, requestBody) // "photo" debe coincidir con el nombre esperado por el backend

        return api.uploadPhoto(userId, photoPart) // devuelve la respuesta de la API y llama al endpoint correspondiente
    }
}