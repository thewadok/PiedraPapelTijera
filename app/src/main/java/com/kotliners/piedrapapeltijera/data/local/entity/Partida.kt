package com.kotliners.piedrapapeltijera.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kotliners.piedrapapeltijera.game.GameResult
import com.kotliners.piedrapapeltijera.game.Move

@Entity(tableName = "partidas")
data class Partida (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fecha: Long = System.currentTimeMillis(),
    val jugadaJugador: Move,
    val jugadaCpu: Move,
    val resultado: GameResult,
    val apuesta: Int,
    val cambioMonedas: Int
)