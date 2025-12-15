package com.kotliners.piedrapapeltijera.data.repository.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * Repositorio de autenticación y jugador remoto
 * Manejamos Firebase Auth y datos del jugador en Firebase DB
 */
class AuthRepository {

    // Firebase Authentication
    private val auth = FirebaseAuth.getInstance()

    // Referencia raíz de Firebase Realtime Database
    private val db = FirebaseDatabase.getInstance().reference

    // Devolvemos el UID del usuario autenticado o null
    fun currentUid(): String? =
        auth.currentUser?.uid

    // Cierramos sesión en Firebase
    fun signOut() =
        auth.signOut()

    // Referencia al nodo /jugadores/{uid}
    private fun jugadorRef(uid: String) =
        db.child("jugadores").child(uid)

    // Comprobamos si el jugador ya existe en la base de datos
    fun jugadorExiste(
        uid: String,
        onResult: (Boolean) -> Unit,
        onError: () -> Unit
    ) {
        jugadorRef(uid).get()
            .addOnSuccessListener { snapshot ->
                onResult(snapshot.exists())
            }
            .addOnFailureListener {
                onError()
            }
    }

    // Creamos un jugador nuevo con valores iniciales
    fun crearJugador(
        uid: String,
        nombre: String,
        onOk: () -> Unit,
        onError: () -> Unit
    ) {
        val jugador = mapOf(
            "uid" to uid,
            "nombre" to nombre,
            "monedas" to 100,
            "victorias" to 0,
            "derrotas" to 0
        )

        jugadorRef(uid).setValue(jugador)
            .addOnSuccessListener { onOk() }
            .addOnFailureListener { onError() }
    }
}