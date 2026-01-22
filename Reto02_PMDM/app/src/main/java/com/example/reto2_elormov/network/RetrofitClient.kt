package com.example.reto2_elormov.network

import com.example.reto2_elormov.data.api.ElorServApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente Retrofit singleton para la comunicación con ElorServ.
 *
 * Uso del patrón Singleton (object) para garantizar:
 * - Una única instancia de Retrofit en toda la aplicación.
 * - Thread-safety nativo de Kotlin.
 * - Acceso directo sin necesidad de instanciar:  RetrofitClient.elorServApi

 */
object RetrofitClient {

    /**
     * Base URL del servidor ElorServ.
     */
    private const val BASE_URL = "http://10.5.104.109:8080"

    /**
     * Interceptor HTTP para logging de peticiones y respuestas.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Cliente OkHttp con configuración de timeouts y logging.
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit. SECONDS)
        .build()

    /**
     * Instancia única de Retrofit.
     * Se crea de forma lazy (sólo cuando se accede por primera vez).
     */
    private val logging by lazy {
        HttpLoggingInterceptor().apply {
            // Muestra cuerpo de petición y respuesta (solo para desarrollo)
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val okHttp by lazy {
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val elorServApi: ElorServApi by lazy {
        retrofit.create(ElorServApi::class.java)
    }
}