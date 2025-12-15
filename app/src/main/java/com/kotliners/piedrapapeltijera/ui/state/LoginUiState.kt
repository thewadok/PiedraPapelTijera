package com.kotliners.piedrapapeltijera.ui.state

// Modelo que usaremos en la UI para mostrar el estado de la pantalla de Login.
data class LoginUiState(
    // Indicamos si el login está en progreso
    val loading: Boolean = false,

    // Muestramos el diálogo para elegir nombre la primera vez
    val showNameDialog: Boolean = false,

    // UID autenticado pendiente de crear en Firebase Database
    val pendingUid: String? = null,

    // Recurso de error
    val errorRes: Int? = null
)