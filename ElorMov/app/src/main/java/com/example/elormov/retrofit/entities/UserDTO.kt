package com.example.elormov.retrofit.entities

import java.io.Serializable

data class UserDTO(
    val id: Int,
    val username: String,
    val email: String,
    val nombre: String,
    val apellidos: String,
    val dni: String,
    val direccion: String,
    val telefono1: String,
    val telefono2: String,
    val argazkiaUrl: String,
    val tipo: TipoDTO
): Serializable
