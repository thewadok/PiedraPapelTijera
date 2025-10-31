package com.kotliners.piedrapapeltijera.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Jugador",
    indices = [Index(value = ["nombre_usuario"], unique = true)]
)
data class Jugador(
    @PrimaryKey(autoGenerate = true)
    val id_jugador: Int = 0,
    val nombre_usuario: String,
    val total_victorias: Int = 0,
    val total_derrotas: Int = 0,
    val total_empates: Int = 0
)