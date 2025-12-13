package com.kotliners.piedrapapeltijera.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

// ViewModel encargado de gestionar la autenticación del usuario
class AuthViewModel(app: Application) : AndroidViewModel(app){
    private val auth = FirebaseAuth.getInstance()

    // Devuelvemos el usuario que tiene la sesión iniciada
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // Cierramos la sesión del usuario actual
    fun signOut() {
        auth.signOut()
    }
}