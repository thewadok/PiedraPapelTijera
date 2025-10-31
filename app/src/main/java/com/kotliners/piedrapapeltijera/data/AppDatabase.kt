package com.kotliners.piedrapapeltijera.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Jugador::class, HistorialPartida::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "piedra_papel_tijera.db"
                )
                    // .fallbackToDestructiveMigration() // solo en desarrollo si cambias el schema
                    .build()

                INSTANCE = instance
                instance
            }
    }
}
