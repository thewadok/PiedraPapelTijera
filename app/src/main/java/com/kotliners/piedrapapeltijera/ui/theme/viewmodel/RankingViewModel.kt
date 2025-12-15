package com.kotliners.piedrapapeltijera.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotliners.piedrapapeltijera.data.remote.firebase.JugadorRemoto
import com.kotliners.piedrapapeltijera.data.repository.remote.FirebaseGameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel encargado de gestionar el ranking de jugadores
class RankingViewModel(

    // Repositorio que obtiene los datos desde Firebase
    private val repository: FirebaseGameRepository = FirebaseGameRepository()
) : ViewModel() {
    // Lista de jugadores del ranking
    private val _topJugadores = MutableStateFlow<List<JugadorRemoto>>(emptyList())
    val topJugadores: StateFlow<List<JugadorRemoto>> = _topJugadores

    // Indicamos si los datos se están cargando
    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    // Mensaje de error en caso de que falle la carga
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Al crear el ViewModel se carga automáticamente el ranking
    init {
        cargarTop10()
    }

    // Obtiene el Top 10 de jugadores desde el repositorio
    fun cargarTop10() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                _topJugadores.value = repository.fetchTopJugadores()
            } catch (e: Exception) {
                // Si ocurre un error, mostramos un mensaje
                _error.value = "Error al cargar el ranking"
            } finally {
                // Finaliza el estado de carga
                _loading.value = false
            }
        }
    }
}