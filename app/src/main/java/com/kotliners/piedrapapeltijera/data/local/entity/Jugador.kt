package com.kotliners.piedrapapeltijera.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jugador")
data class Jugador (
    @PrimaryKey(autoGenerate = true)
    val id_jugador: Int = 0,
    val nombre: String = "Jugador",
    val monedas: Int = 100
)