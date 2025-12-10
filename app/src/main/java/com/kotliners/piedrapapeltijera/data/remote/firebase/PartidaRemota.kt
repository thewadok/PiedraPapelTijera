package com.kotliners.piedrapapeltijera.data.remote.firebase

data class PartidaRemota(
    val uid: String? = null,
    val fecha: Long? = null,
    val jugadaJugador: String? = null,
    val jugadaCpu: String? = null,
    val resultado: String? = null,
    val apuesta: Int? = null,
    val cambioMonedas: Int? = null,
    val latitud: Double? = null,
    val longitud: Double? = null
)
