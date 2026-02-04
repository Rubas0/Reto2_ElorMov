package com.example.elormov.ui.home.horario

data class ClassSlot(
    val hour: String,
    val monday: String = "",
    val tuesday: String = "",
    val wednesday: String = "",
    val thursday: String = "",
    val friday: String = "",
    val mondayReunion: String = "",
    val tuesdayReunion: String = "",
    val wednesdayReunion: String = "",
    val thursdayReunion: String = "",
    val fridayReunion: String = "",
    val mondayReunionEstado: String? = null,
    val tuesdayReunionEstado: String? = null,
    val wednesdayReunionEstado: String? = null,
    val thursdayReunionEstado: String? = null,
    val fridayReunionEstado: String? = null
)