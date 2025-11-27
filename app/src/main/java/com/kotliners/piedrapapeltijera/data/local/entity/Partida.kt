package com.kotliners.piedrapapeltijera.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kotliners.piedrapapeltijera.game.GameResult
import com.kotliners.piedrapapeltijera.game.Move
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "partidas",
    foreignKeys = [
        ForeignKey(
            entity = Jugador::class,
            parentColumns = ["id_jugador"],
            childColumns = ["jugadorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("jugadorId")]
)
data class Partida (
    @PrimaryKey(
        autoGenerate = true
    )
    val id: Int = 0,
    val jugadorId: Int,
    val fecha: Long = System.currentTimeMillis(),
    val jugadaJugador: Move,
    val jugadaCpu: Move,
    val resultado: GameResult,
    val apuesta: Int,
    val cambioMonedas: Int,
    val latitud: Double? = null,
    val longitud: Double? = null
)