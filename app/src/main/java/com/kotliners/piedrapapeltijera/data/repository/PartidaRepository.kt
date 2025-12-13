package com.kotliners.piedrapapeltijera.data.repository

import com.kotliners.piedrapapeltijera.data.local.dao.PartidaDao
import com.kotliners.piedrapapeltijera.data.local.entity.Partida
import com.kotliners.piedrapapeltijera.game.GameResult
import com.kotliners.piedrapapeltijera.game.Move
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers

class PartidaRepository(private val dao: PartidaDao) {
    fun registrarPartida(
        idJugador: String,
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
            jugadorId = idJugador,
            jugadaJugador = jugadaJugador,
            jugadaCpu = jugadaCpu,
            resultado = resultado,
            apuesta = apuesta,
            cambioMonedas = cambio,
            latitud = latitud,
            longitud = longitud
        )
        return dao.insertar(p).subscribeOn(Schedulers.io())
    }

    fun observarTotalPartidas(idJugador: String): Flowable<Int> =
        dao.observarTotalPartidas(idJugador).subscribeOn(Schedulers.io())

    fun observarHistorial(idJugador: String): Flowable<List<Partida>> =
        dao.observarHistorial(idJugador).subscribeOn(Schedulers.io())

    fun borrarHistorial(idJugador: String): Completable = dao.borrarTodo(idJugador).subscribeOn(Schedulers.io())
}