package com.kotliners.piedrapapeltijera.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kotliners.piedrapapeltijera.data.local.dao.JugadorDao
import com.kotliners.piedrapapeltijera.data.local.dao.PartidaDao
import com.kotliners.piedrapapeltijera.data.local.entity.Jugador
import com.kotliners.piedrapapeltijera.data.local.entity.Partida

@Database(
    entities = [Jugador::class, Partida::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jugadorDao(): JugadorDao
    abstract fun partidaDao(): PartidaDao
}