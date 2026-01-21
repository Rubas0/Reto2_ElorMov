package com.example.reto2_elormov.data.dto.repository

import com.elormov.data.api.ElorServApi
import com.elormov.data.dto.LoginRequest
import com.elormov.data.dto.LoginResponse
import retrofit2.Response

class AuthRepository(private val api: ElorServApi) {

    suspend fun login(username: String, password: String): Response<LoginResponse> {
        return api.login(LoginRequest(username, password))
    }
}