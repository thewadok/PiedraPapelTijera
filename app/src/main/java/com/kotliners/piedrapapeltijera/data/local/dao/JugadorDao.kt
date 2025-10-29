package com.kotliners.piedrapapeltijera.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kotliners.piedrapapeltijera.data.local.entity.Jugador
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
interface JugadorDao {

    @Insert
    fun insertarJugador(jugador: Jugador): Single<Long>

    // Obtener el jugador
    @Query("SELECT * FROM jugador LIMIT 1")
    fun obtenerJugador(): Maybe<Jugador>

    // Actualizar monedas del jugador
    @Query("UPDATE jugador SET monedas = :nuevasMonedas WHERE id_jugador = :idJugador")
    fun actualizarMonedas(idJugador: Int, nuevasMonedas: Int): Completable
}