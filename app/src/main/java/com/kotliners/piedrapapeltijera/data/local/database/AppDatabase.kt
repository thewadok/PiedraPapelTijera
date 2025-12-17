package com.kotliners.piedrapapeltijera.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kotliners.piedrapapeltijera.data.local.dao.JugadorDao
import com.kotliners.piedrapapeltijera.data.local.dao.PartidaDao
import com.kotliners.piedrapapeltijera.data.local.entity.Jugador
import com.kotliners.piedrapapeltijera.data.local.entity.Partida

@Database(
    entities = [Jugador::class, Partida::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jugadorDao(): JugadorDao
    abstract fun partidaDao(): PartidaDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "piedra_papel_tijera_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}