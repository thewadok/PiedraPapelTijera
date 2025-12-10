package com.kotliners.piedrapapeltijera.data.remote.firebase


// Modelo del jugador en Firebase
data class JugadorRemoto(
    val uid: String? = null,
    val nombre: String? = null,
    val monedas: Int? = null,
    val victorias: Int? = null,
    val derrotas: Int? = null
)

