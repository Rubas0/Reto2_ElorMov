package com.example.reto2_elormov.data.dto.repository

import com.example.reto2_elormov.data.api.ElorServApi
import com.example.reto2_elormov.data.dto.LoginRequestDTO
import com.example.reto2_elormov.data.dto.LoginResponseDTO
import retrofit2.Response

class AuthRepository(private val api: ElorServApi) {

    /*
    Capa intermedia que llama a la API. Realiza login contra ElorServ
     */
    suspend fun login(username: String, password: String): Response<LoginResponseDTO> {
        return api.login(LoginRequestDTO(username, password))
    }
}