package com.kotliners.piedrapapeltijera.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jugador")
data class Jugador (
    @PrimaryKey
    val id_jugador: String,
    val nombre: String?,
    val email: String?,
    val monedas: Int = 100
)