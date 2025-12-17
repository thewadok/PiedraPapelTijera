package com.kotliners.piedrapapeltijera.data.repository.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.ServerValue
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.kotliners.piedrapapeltijera.data.remote.firebase.PremioComunRemoto

/**
 * Repositorio encargado de gestionar el premio común en Firebase Realtime Database.
 *
 * Nodo: premioComun
 *  - monedasEnBote: Int
 *  - ultimoGanadorUid: String
 */
class PremioRepository (

    // Acceso al backend de Firebase
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    ) {

        // Nodo raíz del premio común
        private val premioRef = database.getReference("premioComun")

        // Subnodo que guarda el número de monedas en el bote
        private val monedasRef = premioRef.child("monedasEnBote")

        // Sumamos la apuesta al bote cuando un jugador pierde sin riesgo de condiciones de carrera.
        fun aumentarPremio(apuesta: Int) {
            if (apuesta <= 0) return
            monedasRef.setValue(ServerValue.increment(apuesta.toLong()))
        }

        // Intentamos entregar el premio al ganador usando una transacción segura.
        fun entregarPremioSiHay(
            uidJugador: String,
            nombreJugador: String,
            onResultado: (premioGanado: Int) -> Unit
        ) {
            // Variable para guardar el valor del bote antes de resetearlo
            var boteGanado = 0

            premioRef.runTransaction(object : Transaction.Handler {

                override fun doTransaction(currentData: MutableData): Transaction.Result {

                    val boteActual = (currentData.child("monedasEnBote").getValue(Long::class.java)?: 0L).toInt()

                    return if (boteActual > 0) {
                        // Guardamos el valor del bote antes de ponerlo a 0
                        boteGanado = boteActual

                        // Reseteamos el bote
                        currentData.child("monedasEnBote").value = 0

                        // Guardamos info del ganador
                        currentData.child("ultimoGanadorUid").value = uidJugador
                        currentData.child("ultimoGanadorNombre").value = nombreJugador

                        // Guardamos cuánto se llevó (para UI)
                        currentData.child("ultimoPremioGanado").value = boteGanado

                        // Evento único (para evitar duplicados en UI)
                        currentData.child("ultimoEventoId").value = ServerValue.TIMESTAMP

                        Transaction.success(currentData)
                    } else {
                        Transaction.abort()
                    }
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    snapshot: DataSnapshot?
                ) {
                    if (error != null || !committed) {
                        onResultado(0)
                    } else {
                        // Si la transacción se ha aplicado, devolvemos el bote que había
                        onResultado(boteGanado)
                    }
                }
            })
        }

        // Obtenemos los cambios del premio en tiempo real.
        fun observarPremio(onCambio: (PremioComunRemoto) -> Unit): ValueEventListener {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val premio = snapshot.getValue(PremioComunRemoto::class.java)
                    if (premio != null) {
                        onCambio(premio)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }

            premioRef.addValueEventListener(listener)
            return listener
        }

        // Dejamos de obtener los cambios del premio cuando el ViewModel se destruye.
        fun dejarDeObservar(listener: ValueEventListener) {
            premioRef.removeEventListener(listener)
        }
}