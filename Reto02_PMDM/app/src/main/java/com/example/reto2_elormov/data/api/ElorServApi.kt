package com.example.reto2_elormov.data.api

import com.example.reto2_elormov.data.dto.LoginRequestDTO
import com.example.reto2_elormov.data.dto.LoginResponseDTO
import com.example.reto2_elormov.data.dto.ResetPasswordRequestDto
import com.example.reto2_elormov.data.dto.ResetPasswordResponseDto
import com.example.reto2_elormov.data.dto.UploadPhotoResponseDTO
import com.example.reto2_elormov.data.dto.UserDTO
import com.example.reto2_elormov.data.dto.HorarioSemanalDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ElorServApi {
    /*
    Interfaz Retrofit con el endpoint /api/auth/login. Realiza una solicitud POST para autenticar a un usuario.
     */
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequestDTO): Response<LoginResponseDTO>

    /*
    Nuevo endpoint para restablecer la contraseña
     */
    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto): Response<ResetPasswordResponseDto>

    // Obtener perfil del usuario
    @GET("api/users/{id}")
    suspend fun getProfile(@Path("id") userId: Int): Response<UserDTO>


    /**
    Endpoint para subir una foto de perfil, funciona de tal manera que se envía el id del usuario en la ruta y la foto como parte multiparte, es decir, en el cuerpo de la solicitud.
    Y devuelve un UploadPhotoResponseDTO que contiene la URL de la foto subida.
     */
    @Multipart
    @POST("api/users/{id}/argazkiaUrl")
    suspend fun uploadPhoto(
        @Path("id") userId: Int,
        @Part photo: MultipartBody.Part
    ): Response<UploadPhotoResponseDTO>


    /**
     * - Obtener horario del profesor
     * @param profesorId ID del profesor
     * @param semana Número de semana (
     */
    @GET("horarios/profesor/{id}")
    suspend fun getHorarioProfesor(
        @Path("id") profesorId: Int,
        @Query("semana") semana: Int? = null
    ): Response<HorarioSemanalDto>

    /**
     * - Obtener horario del alumno (generado dinámicamente)
     * @param alumnoId ID del alumno
     * @param semana Número de semana
     */
    @GET("horarios/alumno/{id}")
    suspend fun getHorarioAlumno(
        @Path("id") alumnoId: Int,
        @Query("semana") semana: Int? = null
    ): Response<HorarioSemanalDto>
}

