package com.kotliners.piedrapapeltijera.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseUser
import com.kotliners.piedrapapeltijera.data.repository.remote.AuthRepository

// ViewModel encargado de gestionar la autenticación del usuario
class AuthViewModel(
    app: Application,
    private val authRepo: AuthRepository
) : AndroidViewModel(app) {

    // Devuelvemos el usuario que tiene la sesión iniciada
    val currentUser: FirebaseUser?
        get() = authRepo.currentUser()

    // Cierramos la sesión del usuario actual
    fun signOut() {
        authRepo.signOut()
    }
}