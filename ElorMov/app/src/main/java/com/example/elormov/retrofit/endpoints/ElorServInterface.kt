package com.example.elormov.retrofit.endpoints

import com.example.elormov.retrofit.entities.HorarioDTO
import com.example.elormov.retrofit.entities.LoginRequestDTO
import com.example.elormov.retrofit.entities.LoginResponseDTO
import com.example.elormov.retrofit.entities.ResetPasswordRequestDTO
import com.example.elormov.retrofit.entities.ResetPasswordResponseDTO
import com.example.elormov.retrofit.entities.ReunionDTO
import com.example.elormov.retrofit.entities.UploadPhotoResponseDTO
import com.example.elormov.retrofit.entities.UserDTO
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ElorServInterface {


    // ======================= GET =======================

    // Obtener lista de usuarios
    @GET("api/users")
    fun getUsers(): Call<List<UserDTO>>

    // Obtener lista de usuarios por tipo
    @GET("api/users/tipo/{tipo}")
    fun getUsersByType(@Path("tipo") tipo: String): Call<List<UserDTO>>

    // Obtener perfil de usuario por id
    @GET("api/users/{id}")
    fun getProfile(@Path("id") userId: Int): Call<UserDTO>

    // Obtener horarios de un profesor
    @GET("api/horarios/profesor/{id}")
    fun getHorariosProfesor(@Path("id") profesorId: Int): Call<List<HorarioDTO>>

    // Obtener horarios de un alumno
    @GET("api/horarios/alumno/{id}")
    fun getHorariosAlumno(@Path("id") alumnoId: Int): Call<List<HorarioDTO>>

    // Obtener reuniones de un profesor
    @GET("api/reuniones/profesor/{id}")
    fun getReunionesProfesor(@Path("id") profesorId: Int): Call<List<ReunionDTO>>

    // Obtener reuniones de un alumno
    @GET("api/reuniones/alumno/{id}")
    fun getReunionesAlumno(@Path("id") alumnoId: Int): Call<List<ReunionDTO>>

    // Obtener foto de perfil de un usuario
    @GET("api/users/{id}/argazkiaUrl")
    suspend fun getPfp(@Path("id") userId: Int): Response<ResponseBody>

    // Obtener alumnos de un profesor
    @GET("api/users/profesor/{id}/students")
    fun getAlumnosDelProfesor(@Path("id") profesorId: Int): Call<List<UserDTO>>

    // ======================= POST =======================

    // autenticar login.
    @POST("/api/auth/login")
    fun login(@Body request: LoginRequestDTO): Call<LoginResponseDTO>

    // restablecer la contraseña
    @POST("api/auth/reset-password")
    fun resetPassword(@Body request: ResetPasswordRequestDTO): Call<ResetPasswordResponseDTO>

    // Subir foto de perfil
    @Multipart
    @POST("api/users/{id}/argazkiaUrl")
    suspend fun uploadPhoto(
        @Path("id") userId: Int,
        @Part file: MultipartBody.Part
    ): Response<UploadPhotoResponseDTO>

}