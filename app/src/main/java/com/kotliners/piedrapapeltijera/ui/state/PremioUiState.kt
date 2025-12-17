package com.kotliners.piedrapapeltijera.ui.state

// Modelo que usaremos en la UI para mostrar el estado del premio común.
 data class PremioUiState (
    val monedasEnBote: Int = 0,
    val ultimoGanadorUid: String = "",
    val ultimoGanadorNombre: String = "",
    val ultimoPremioGanado: Int = 0,
    val ultimoEventoId: Long = 0L
)