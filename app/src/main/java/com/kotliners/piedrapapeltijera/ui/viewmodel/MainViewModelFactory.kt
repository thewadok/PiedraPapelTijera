package com.kotliners.piedrapapeltijera.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kotliners.piedrapapeltijera.data.repository.JugadorRepository
import com.kotliners.piedrapapeltijera.data.repository.PartidaRepository

class MainViewModelFactory (
    private val repo: JugadorRepository,
    private val historial: PartidaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repo, historial) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
