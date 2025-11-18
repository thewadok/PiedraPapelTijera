package com.kotliners.piedrapapeltijera.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kotliners.piedrapapeltijera.data.local.entity.Partida
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface PartidaDao {

    @Insert
    fun insertar(partida: Partida): Completable

    @Query("SELECT * FROM partidas WHERE jugadorId = :jugadorId ORDER BY fecha DESC")
    fun observarHistorial(jugadorId: Int): Flowable<List<Partida>>

    @Query("SELECT COUNT(*) FROM partidas WHERE jugadorId = :jugadorId")
    fun contar(jugadorId: Int): Single<Int>

    @Query("DELETE FROM partidas WHERE jugadorId = :jugadorId")
    fun borrarTodo(jugadorId: Int): Completable

    @Query("SELECT COUNT(*) FROM partidas WHERE jugadorId = :jugadorId")
    fun observarTotalPartidas(jugadorId: Int): Flowable<Int>

}