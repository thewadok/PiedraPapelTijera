package com.kotliners.piedrapapeltijera.ui.state

// Modelo que usaremos en la UI para mostrar el estado del premio común.
 data class PremioViewState (
    val monedasEnBote: Int = 0,
    val ultimoGanadorUid: String = "",
    val ultimoPremioGanado: Int = 0
)