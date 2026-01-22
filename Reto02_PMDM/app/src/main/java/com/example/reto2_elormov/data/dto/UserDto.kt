package com.example.reto2_elormov.data.dto

data class UserDTO(
   val id:  Int?,
   val username: String?,
   val email: String?,
   val nombre: String?,
   val apellidos: String?,
   val dni: String?,
   val direccion: String?,
   val telefono1: String?,
   val telefono2: String?,
   val argazkiaUrl: String?,
   val tipo: TipoDTO?
)