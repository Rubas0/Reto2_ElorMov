package com.example.elormov.retrofit.client

import com.example.elormov.retrofit.endpoints.ElorServInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080") // localhost from the Android emulator
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val elorServInterface: ElorServInterface = retrofit.create(ElorServInterface::class.java)
}
