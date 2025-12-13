package com.kotliners.piedrapapeltijera.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kotliners.piedrapapeltijera.data.local.entity.Jugador
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.Flowable

@Dao
interface JugadorDao {

    // Cambiamos a 'OnConflictStrategy.IGNORE' para que si intentamos insertar un jugador
    // que ya existe (mismo UID), simplemente no haga nada y no dé un error.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertarJugador(jugador: Jugador): Completable

    // Ahora, para obtener un jugador, necesitamos saber su ID.
    @Query("SELECT * FROM jugador WHERE id_jugador = :id")
    fun obtenerJugador(id: String): Maybe<Jugador>

    // Para observar el saldo de monedas, también necesitamos el ID del jugador.
    @Query("SELECT monedas FROM jugador WHERE id_jugador = :id")
    fun observarMonedas(id: String): Flowable<Int>

    // Para actualizar las monedas, usamos el ID de tipo String.
    @Query("UPDATE jugador SET monedas = :nuevasMonedas WHERE id_jugador = :id")
    fun actualizarMonedas(id: String, nuevasMonedas: Int): Completable

    // Para sumar/restar, también necesitamos saber a qué jugador.
    @Query("UPDATE jugador SET monedas = monedas + :cantidad WHERE id_jugador = :id")
    fun sumarRestarMonedas(id: String, cantidad: Int): Completable

}