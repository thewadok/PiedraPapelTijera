package com.kotliners.piedrapapeltijera

import android.app.Application
import androidx.room.Room
import com.kotliners.piedrapapeltijera.data.local.database.AppDatabase

class MyApp : Application() {

    companion object {
        lateinit var db: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "piedra_papel_tijera.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}