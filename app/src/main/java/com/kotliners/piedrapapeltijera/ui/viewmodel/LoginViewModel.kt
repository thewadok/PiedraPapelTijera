package com.kotliners.piedrapapeltijera.ui.viewmodel

class LoginViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {
    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onGoogleLoginSuccess(uid: String, displayName: String?, onLoginOk: () -> Unit) {
        uiState = uiState.copy(loading = true, errorRes = null)

        repo.jugadorExiste(
            uid = uid,
            onResult = { exists ->
                uiState = uiState.copy(loading = false)
                if (exists) onLoginOk()
                else uiState = uiState.copy(
                    showNameDialog = true,
                    pendingUid = uid
                )
            },
            onError = {
                uiState = uiState.copy(loading = false, errorRes = R.string.login_firebase_error)
            }
        )
    }

    fun onConfirmName(nombre: String, onLoginOk: () -> Unit) {
        val uid = uiState.pendingUid ?: return
        val clean = nombre.trim()
        if (clean.isBlank()) return

        uiState = uiState.copy(loading = true)

        repo.crearJugador(
            uid = uid,
            nombre = clean,
            onOk = {
                uiState = uiState.copy(loading = false, showNameDialog = false, pendingUid = null)
                onLoginOk()
            },
            onError = {
                uiState = uiState.copy(loading = false, errorRes = R.string.login_firebase_error)
            }
        )
    }
}