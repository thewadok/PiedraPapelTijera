package com.kotliners.piedrapapeltijera.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(Converters::class)
@Database(entities = [Jugador::class, HistorialPartida::class], version = 1)

abstract class AppDatabase() : RoomDatabase() {

    abstract fun gameDao(): GameDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase { // Función para obtener la DB

            // Si la instancia ya existe, la devuelve.
            // Si es nula, entra en el bloque 'synchronized' para crearla
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "piedra_papel_tijera.db"
                )
                .allowMainThreadQueries() // Permite consultas en el hilo principal
                .fallbackToDestructiveMigrationOnDowngrade() // Permite la destrucción de la BD si se baja de versión
                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}