package com.kotliners.piedrapapeltijera.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.kotliners.piedrapapeltijera.data.Jugada
import com.kotliners.piedrapapeltijera.data.Jugador
import com.kotliners.piedrapapeltijera.data.Resultado

@Entity(
    tableName = "HistorialPartida",
    foreignKeys = [
        ForeignKey(
            entity = Jugador::class,
            parentColumns = ["id_jugador"],
            childColumns = ["id_jugador"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ]
)
data class HistorialPartida(
    @PrimaryKey(autoGenerate = true)
    val id_partida: Int = 0,
    val id_jugador: Int,
    val jugada_jugador: Jugada,
    val jugada_cpu: Jugada,
    val resultado: Resultado,
    val fecha_partida: Long = System.currentTimeMillis()
)