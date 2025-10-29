package com.kotliners.piedrapapeltijera.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kotliners.piedrapapeltijera.data.local.dao.JugadorDao
import com.kotliners.piedrapapeltijera.data.local.entity.Jugador

@Database(
    entities = [Jugador::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jugadorDao(): JugadorDao
}