package com.kotliners.piedrapapeltijera.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotliners.piedrapapeltijera.data.remote.firebase.JugadorRemoto
import com.kotliners.piedrapapeltijera.data.repository.remote.FirebaseGameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RankingViewModel(
    private val repository: FirebaseGameRepository = FirebaseGameRepository()
) : ViewModel() {

    private val _topJugadores = MutableStateFlow<List<JugadorRemoto>>(emptyList())
    val topJugadores: StateFlow<List<JugadorRemoto>> = _topJugadores

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        cargarTop10()
    }

    fun cargarTop10() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                _topJugadores.value = repository.fetchTopJugadores()
            } catch (e: Exception) {
                _error.value = "Error al cargar el ranking"
            } finally {
                _loading.value = false
            }
        }
    }
}