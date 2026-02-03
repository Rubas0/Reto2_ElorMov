package com.example.elormov.retrofit.entities

data class ModuloDTO (
    val id: Int,
    val nombre: String,
    val nombreEus: String,
    val horas: Int,
    val curso: Int,
    val ciclo: CicloDTO
)