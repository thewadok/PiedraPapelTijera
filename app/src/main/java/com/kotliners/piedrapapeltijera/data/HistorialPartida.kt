package com.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "HistorialPartida",
    foreignKeys = [
        ForeignKey(
            entity = Jugador::class,
            parentColumns = ["id_jugador"], // Definimos la Clave Foránea y sus restricciones (Foreign Key)
            childColumns = ["id_jugador"],
            onDelete = ForeignKey.CASCADE // Restricción si se borra un jugador, se borra su historial
        )
    ]
)

data class HistorialPartida(

    @PrimaryKey(autoGenerate = true)
    val id_partida: Int = 0,
    val id_jugador: Int, // Esta es la clave que nos une a tabla Jugador
    val jugada_jugador: Jugada,
    val jugada_cpu: Jugada,
    val resultado: Resultado,
    val fecha_partida: String = System.currentTimeMillis().toString() // / Room puede guardar timestamps, pero para SQLite simple, usamos TEXT para guardar fechas

)
