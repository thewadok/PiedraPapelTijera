package com.kotliners.piedrapapeltijera.data.repository

import com.kotliners.piedrapapeltijera.data.local.dao.JugadorDao
import com.kotliners.piedrapapeltijera.data.local.entity.Jugador
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.schedulers.Schedulers

class JugadorRepository(
    private val dao: JugadorDao
) {
    // Garantizamos que exista un jugador (creamos uno si no hay)
    fun ensureJugador(): Completable {
        return dao.obtenerJugador()
            .isEmpty
            .flatMapCompletable { empty ->
                if (empty) dao.insertarJugador(Jugador())
                    .ignoreElement()
                else Completable.complete()
            }
            .subscribeOn(Schedulers.io())
    }

    // Obtenemos el saldo para la UI
    fun observarMonedas(): Flowable<Int> =
        dao.observarMonedas()
            .subscribeOn(Schedulers.io())

    // Establecemos un nuevo saldo absoluto
    fun setMonedas(nuevoSaldo: Int): Completable {
        return dao.obtenerJugador()
            .switchIfEmpty(Maybe.error(Throwable("No hay jugador")))
            .flatMapCompletable { jugador ->
                dao.actualizarMonedas(jugador.id_jugador, nuevoSaldo)
            }
            .subscribeOn(Schedulers.io())
    }

    // Cambiamos el saldo relativo +gana / -pierde
    fun cambiarMonedas(cantidad: Int): Completable {
        return dao.sumarRestarMonedas(cantidad)
            .subscribeOn(Schedulers.io())
    }
}