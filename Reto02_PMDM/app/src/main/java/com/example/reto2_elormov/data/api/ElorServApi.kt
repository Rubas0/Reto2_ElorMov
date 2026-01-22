package com.example.reto2_elormov.data.api

import com.example.reto2_elormov.data.dto.LoginRequestDTO
import com.example.reto2_elormov.data.dto.LoginResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ElorServApi {
    /*
    Interfaz Retrofit con el endpoint /api/auth/login. Realiza una solicitud POST para autenticar a un usuario.
     */
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequestDTO): Response<LoginResponseDTO>
}