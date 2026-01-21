package com.example.reto2_elormov.ui.login

import android.content.Context
import com.elormov.data.api.ElorServApi
import com.elormov.data.repository.AuthRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Injector {
    /*
    Un injector (inyector) es un componente que se encarga de proporcionar las dependencias que necesita una clase,
    en lugar de que la clase las cree por sí misma. Es parte del patrón de diseño llamado Dependency Injection (Inyección de Dependencias).
     */
    private fun retrofit(): Retrofit {
        val client = OkHttpClient.Builder().build()
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080") // Emulador Android → host máquina
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun loginViewModel(context: Context): LoginViewModel {
        val api = retrofit().create(ElorServApi::class.java)
        val repo = AuthRepository(api)
        return LoginViewModel(repo)
    }
}