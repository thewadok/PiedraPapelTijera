package com.kotliners.piedrapapeltijera.utils.auth

import com.google.firebase.auth.FirebaseAuth

object AuthHelper {

    // Devuelve true si el usuario ya ha iniciado sesión
    fun isLoggedIn(): Boolean =
        FirebaseAuth.getInstance().currentUser != null

    // Cierra la sesión y elimina la autenticación guardada
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}