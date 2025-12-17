package com.kotliners.piedrapapeltijera.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ValueEventListener
import com.kotliners.piedrapapeltijera.data.remote.firebase.PremioComunRemoto
import com.kotliners.piedrapapeltijera.data.repository.remote.PremioRepository
import com.kotliners.piedrapapeltijera.ui.state.PremioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel que conecta la UI con el PremioRepository.
 * Se encarga de escuchar el premio en tiempo real y de
 * llamar al repositorio cuando un jugador gana o pierde.
 */
class PremioViewModel(
    private val premioRepository: PremioRepository = PremioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PremioUiState())
    val uiState: StateFlow<PremioUiState> = _uiState

    private var premioListener: ValueEventListener? = null

    init {
        // Empezamos a obtener el premio en tiempo real al crear el ViewModel.
        premioListener = premioRepository.observarPremio { premio: PremioComunRemoto ->
            _uiState.update {
                it.copy(
                    monedasEnBote = (premio.monedasEnBote ?: 0L).toInt(),
                    ultimoGanadorUid = premio.ultimoGanadorUid.orEmpty(),
                    ultimoGanadorNombre = premio.ultimoGanadorNombre.orEmpty(),
                    ultimoPremioGanado = (premio.ultimoPremioGanado ?: 0L).toInt(),
                    ultimoEventoId = premio.ultimoEventoId ?: 0L
                )
            }
        }
    }

    // Llamamos cuando el jugador pierde una partida.
    fun onJugadorPierde(apuesta: Int) {
        premioRepository.aumentarPremio(apuesta)
    }

    // Llamamos cuando el jugador gana una partida.
    fun onJugadorGana(
        uidJugador: String,
        nombreJugador: String,
        onPremioEntregado: (Int) -> Unit
        ) {
        premioRepository.entregarPremioSiHay(uidJugador, nombreJugador)  { premioGanado ->
            if (premioGanado > 0) {
                onPremioEntregado(premioGanado)
            }
        }
    }

    // Marcamos que ya hemos usado el premio en la UI.
    fun marcarPremioConsumido() {
        _uiState.update { it.copy(ultimoPremioGanado = 0) }
    }

    override fun onCleared() {
        super.onCleared()
        // Dejamos de obtener el premio cuando se destruye el ViewModel.
        premioListener?.let {
            premioRepository.dejarDeObservar(it)
        }
    }
}