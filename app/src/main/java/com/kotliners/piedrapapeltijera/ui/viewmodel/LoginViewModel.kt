package com.kotliners.piedrapapeltijera.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.kotliners.piedrapapeltijera.R
import com.kotliners.piedrapapeltijera.data.repository.remote.AuthRepository
import com.kotliners.piedrapapeltijera.ui.state.LoginUiState

class LoginViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    // Estado de la pantalla de login
    var uiState by mutableStateOf(LoginUiState())
        private set

    /**
     * Lo llamamos cuando el login con Google ha sido correcto.
     * Comprueba si el jugador ya existe en Firebase
     * - Si existe entramos a Home
     * - Si no existe muestramos el diálogo para elegir nombre
     */
    fun onGoogleLoginSuccess(
        uid: String,
        displayName: String?,
        onLoginOk: () -> Unit
    ) {
        uiState = uiState.copy(loading = true, errorRes = null)

        repo.jugadorExiste(
            uid = uid,
            onResult = { exists ->

                if (exists) {
                    // El jugador ya existe accedemos directo
                    uiState = uiState.copy(loading = false)
                    onLoginOk()
                }
                else {
                    // Primera vez pedimos el nombre
                    uiState = uiState.copy(
                        loading = false,
                        showNameDialog = true,
                        pendingUid = uid,
                        suggestedName = displayName?.trim().orEmpty()
                    )
                }
            },
            onError = {
                // Error al consultar Firebase
                uiState = uiState.copy(
                    loading = false,
                    errorRes = R.string.login_firebase_error
                )
            }
        )
    }

    /**
     * Lo llamamos cuando el usuario confirma su nombre.
     * - Validamos el nombre
     * - Creamos el jugador en Firebase
     * - Accedemos a Home
     */
    fun onConfirmName(nombre: String, onLoginOk: () -> Unit) {
        val uid = uiState.pendingUid

        if (uid.isNullOrBlank()) {
            setError(R.string.login_firebase_error)
            return
        }

        val clean = nombre.trim()

        // Validamos el nombre
        if (clean.isBlank()) {
            uiState = uiState.copy(nameError = true)
            setError(R.string.choose_name_error)
            return
        }

        setLoading(true)
        clearError()

        repo.crearJugador(
            uid = uid,
            nombre = clean,
            onOk = {

                setLoading(false)

                //Jugador creado correctamente
                uiState = uiState.copy(
                    showNameDialog = false,
                    pendingUid = null,
                    suggestedName = "",
                    nameError = false
                    )
                onLoginOk()
            },
            onError = {

                // Error creando el jugador
                setLoading(false)
                setError(R.string.login_firebase_error)
            }
        )
    }

    // Muestramos el error en la UI y finalizamos el estado de carga
    fun setError(resId: Int) {
        uiState = uiState.copy(errorRes = resId, loading = false)
    }

    // Activa o desactiva el estado de carga de la UI
    fun setLoading(value: Boolean) {
        uiState = uiState.copy(loading = value)
    }

    // Limpiamos el error actual
    fun clearError() {
        uiState = uiState.copy(errorRes = null)
    }

    // Actualizamos el nombre mientras el usuario escribe
    fun onNameChanged(value: String) {
        uiState = uiState.copy(
            suggestedName = value,
            nameError = false
        )
    }
}