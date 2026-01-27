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
   val tipoUsuario: TipoDTO?,
   // Campos específicos de ALUMNO
   val ciclo: String?,       // DAM, DAW, ASIR
   val curso: String?,       // 1º, 2º
   val dualIntensiva: Boolean?, // true/false (solo 2º)

   // Campos específicos de PROFESOR
   val departamento: String?,
   val tutorDe: String?      // Ej: "2DAM"
)