package com.kotliners.piedrapapeltijera.data.remote.firebase

// Modelo de premio comun en Firebase
data class PremioComunRemoto(
    val monedasEnBote: Long? = null,
    val ultimoGanadorUid: String? = null,
    val ultimoGanadorNombre: String? = "",
    val ultimoPremioGanado: Long? = null,
    val ultimoEventoId: Long? = null
)

