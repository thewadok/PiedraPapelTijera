package com.kotliners.piedrapapeltijera.data.repository

import com.google.firebase.auth.FirebaseUser
import com.kotliners.piedrapapeltijera.data.local.dao.JugadorDao
import com.kotliners.piedrapapeltijera.data.local.entity.Jugador
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.schedulers.Schedulers

class JugadorRepository(
    private val dao: JugadorDao
) {
    // Garantizamos que exista un jugador (creamos uno si no hay)

    fun ensureJugador(user: FirebaseUser): Completable {
        val jugador = Jugador(
            id_jugador = user.uid,
            nombre = user.displayName,
            email = user.email,
            monedas = 100 // Monedas iniciales para un nuevo jugador
        )
        return dao.insertarJugador(jugador)
            .subscribeOn(Schedulers.io())
    }

    // Obtenemos el saldo para la UI
    fun observarMonedas(id:String): Flowable<Int> =
        dao.observarMonedas(id)
            .subscribeOn(Schedulers.io())

    // Establecemos un nuevo saldo absoluto
    fun setMonedas(id: String, nuevoSaldo: Int): Completable {
        return dao.actualizarMonedas(id, nuevoSaldo)
            .subscribeOn(Schedulers.io())
    }

    // Cambiamos el saldo relativo +gana / -pierde
    fun cambiarMonedas(id: String, cantidad: Int): Completable {
        return dao.sumarRestarMonedas(id, cantidad)
            .subscribeOn(Schedulers.io())
    }
}