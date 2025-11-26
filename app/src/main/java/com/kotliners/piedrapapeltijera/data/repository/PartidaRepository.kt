package com.kotliners.piedrapapeltijera.data.repository

import com.kotliners.piedrapapeltijera.data.local.dao.PartidaDao
import com.kotliners.piedrapapeltijera.data.local.entity.Partida
import com.kotliners.piedrapapeltijera.game.GameResult
import com.kotliners.piedrapapeltijera.game.Move
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers

class PartidaRepository(private val dao: PartidaDao) {

    private val jugadorLocalId = 1
    fun registrarPartida(
        jugadaJugador: Move,
        jugadaCpu: Move,
        resultado: GameResult,
        apuesta: Int,
        latitud: Double?,
        longitud: Double?
    ): Completable {
        val cambio = when (resultado) {
            GameResult.GANAS -> +apuesta
            GameResult.PIERDES -> -apuesta
            GameResult.EMPATE -> 0
        }
        val p = Partida(
            jugadorId = jugadorLocalId,
            jugadaJugador = jugadaJugador,
            jugadaCpu = jugadaCpu,
            resultado = resultado,
            apuesta = apuesta,
            cambioMonedas = cambio
        )
        return dao.insertar(p).subscribeOn(Schedulers.io())
    }

    fun observarTotalPartidas(): Flowable<Int> =
        dao.observarTotalPartidas(jugadorLocalId).subscribeOn(Schedulers.io())

    fun observarHistorial(): Flowable<List<Partida>> =
        dao.observarHistorial(jugadorLocalId).subscribeOn(Schedulers.io())

    fun borrarHistorial(): Completable = dao.borrarTodo(jugadorLocalId).subscribeOn(Schedulers.io())
}