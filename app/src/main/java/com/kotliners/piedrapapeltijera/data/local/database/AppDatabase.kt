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
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)

abstract class AppDatabase : RoomDatabase() {
    abstract fun jugadorDao(): JugadorDao
    abstract fun partidaDao(): PartidaDao

    companion object {

        @Volatile
        private var INSTANCE: com.kotliners.piedrapapeltijera.data.local.database.AppDatabase? = null

        fun getDatabase(context: Context): com.kotliners.piedrapapeltijera.data.local.database.AppDatabase { // Función para obtener la DB

            // Si la instancia ya existe, la devuelve.
            // Si es nula, entra en el bloque 'synchronized' para crearla
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "piedra_papel_tijera.db"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration() // Permite la destrucción de la BD si hay un problema de versión
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}