package com.kotliners.piedrapapeltijera.data.repository.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction


/**
 * Repositorio de autenticación y jugador remoto
 * Manejamos Firebase Auth y datos del jugador en Firebase DB
 */
class AuthRepository {

    // Firebase Authentication
    private val auth = FirebaseAuth.getInstance()

    // Referencia raíz de Firebase Realtime Database
    private val db = FirebaseDatabase.getInstance().reference

    // Devolvemos el UID del usuario autenticado o null si no hay sesión
    fun currentUid(): String? =
        auth.currentUser?.uid

    // Devuelvemos el usuario autenticado completo, o null si no hay sesión
    fun currentUser(): FirebaseUser? = auth.currentUser

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

    // Sumamos o restamos monedas al jugador de forma segura usando una transacción
    fun sumarMonedas(uid: String, delta: Int, onOk: () -> Unit = {}, onError: () -> Unit = {}) {

        jugadorRef(uid).child("monedas")
            .runTransaction(object : Transaction.Handler {

                // Leemos el valor actual y lo actualizamos de forma atómica
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val actual = currentData.getValue(Int::class.java) ?: 0
                    currentData.value = actual + delta
                    return Transaction.success(currentData)
                }

                // Indica si la operación se ha completado correctamente o no.
                override fun onComplete(error: com.google.firebase.database.DatabaseError?, committed: Boolean, snapshot: com.google.firebase.database.DataSnapshot?) {
                    if (error != null || !committed) onError() else onOk()
                }
            })
    }


    // Incrementamos en 1 el número de victorias del jugador de forma segura usando una transacción
    fun sumarVictoria(uid: String, onOk: () -> Unit = {}, onError: () -> Unit = {}) {

        jugadorRef(uid).child("victorias")
            .runTransaction(object : Transaction.Handler {

                // Obtenemos el número actual de victorias y lo incrementa en 1
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val actual = currentData.getValue(Int::class.java) ?: 0
                    currentData.value = actual + 1
                    return Transaction.success(currentData)
                }

                // Callback final de la transacción
                override fun onComplete(error: com.google.firebase.database.DatabaseError?, committed: Boolean, snapshot: com.google.firebase.database.DataSnapshot?) {
                    if (error != null || !committed) onError() else onOk()
                }
            })
    }

    // Incrementamos en 1 el número de derrotas del jugador de forma segura usando una transacción
    fun sumarDerrota(uid: String, onOk: () -> Unit = {}, onError: () -> Unit = {}) {

        jugadorRef(uid).child("derrotas")
            .runTransaction(object : Transaction.Handler {

                // Obtenemos el número actual de derrotas y lo incrementa en 1
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val actual = currentData.getValue(Int::class.java) ?: 0
                    currentData.value = actual + 1
                    return Transaction.success(currentData)
                }

                // Callback final de la transacción
                override fun onComplete(error: com.google.firebase.database.DatabaseError?, committed: Boolean, snapshot: com.google.firebase.database.DataSnapshot?) {
                    if (error != null || !committed) onError() else onOk()
                }
            })
    }

    // Fijamos el saldo exacto de monedas
    fun setMonedas(uid: String, saldo: Int, onOk: () -> Unit = {}, onError: () -> Unit = {}) {
        jugadorRef(uid).child("monedas")
            .setValue(saldo)
            .addOnSuccessListener { onOk() }
            .addOnFailureListener { onError() }
    }

    // Reseteamos estadísticas, victorias/derrotas a 0
    fun resetStats(uid: String, onOk: () -> Unit = {}, onError: () -> Unit = {}) {
        val updates = mapOf<String, Any>(
            "victorias" to 0,
            "derrotas" to 0
        )

        jugadorRef(uid).updateChildren(updates)
            .addOnSuccessListener { onOk() }
            .addOnFailureListener { onError() }
    }
}