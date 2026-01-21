package com.example.reto2_elormov.api

import com.elormov.data.dto.LoginRequest
import com.elormov.data.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ElorServ2 {

    @POST("/api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>
}