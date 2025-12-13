package com.kotliners.piedrapapeltijera.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser

/**
 * Esta es una "fábrica" para nuestro MainViewModel.
 * Su única responsabilidad es saber cómo crear una instancia de MainViewModel
 * cuando este necesita un 'FirebaseUser' en su constructor.
 */
class ViewModelFactory(private val user: FirebaseUser) : ViewModelProvider.Factory {    override fun <T : ViewModel> create(modelClass: Class<T>): T {
    // Comprueba si la clase que se pide crear es MainViewModel.
    if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
        // Si lo es, crea una instancia pasándole el 'user' y la devuelve.
        // La supresión de la advertencia es segura aquí porque hemos comprobado la clase.
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(user) as T
    }
    // Si se pide crear cualquier otro tipo de ViewModel, lanza un error.
    throw IllegalArgumentException("Unknown ViewModel class")
}
}