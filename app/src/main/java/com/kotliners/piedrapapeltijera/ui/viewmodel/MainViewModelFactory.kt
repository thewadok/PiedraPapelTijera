package com.kotliners.piedrapapeltijera.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kotliners.piedrapapeltijera.data.repository.local.JugadorRepository
import com.kotliners.piedrapapeltijera.data.repository.local.PartidaRepository
import com.kotliners.piedrapapeltijera.data.repository.remote.AuthRepository

/**
 * Factory encargada de crear instancias de ViewModels
 * que necesitan dependencias en su constructor.
 *
 * Utilizamos para crear el MainViewModel
 * inyectándole los repositorios necesarios.
 */
class MainViewModelFactory (

    // Repositorio para la gestión de jugadores
    private val repo: JugadorRepository,

    // Repositorio para el historial de partidas
    private val historial: PartidaRepository,

    // Repositorio de autenticación
    private val authRepo: AuthRepository

) : ViewModelProvider.Factory {

    // Creamos el ViewModel solicitado
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // Comprobamos si el ViewModel solicitado es MainViewModel
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {

            // Creamos el MainViewModel pasando las dependencias necesarias
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(
                repo = repo,
                historial = historial,
                authRepo = authRepo
                ) as T
        }

        // Si se solicita un ViewModel no soportado por esta Factory
        throw IllegalArgumentException("Unknown ViewModel")
    }
}