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
    fun insertar(partida: Partida): Long

    @Query("SELECT * FROM partidas ORDER BY fecha DESC")
    fun observarHistorial(): Flowable<List<Partida>>

    @Query("SELECT COUNT(*) FROM partidas")
    fun contar(): Single<Int>

    @Query("DELETE FROM partidas")
    fun borrarTodo(): Completable

    @Query("SELECT COUNT(*) FROM partidas")
    fun observarTotalPartidas(): Flowable<Int>
}