package com.kotliners.piedrapapeltijera.data

import android.app.Application
import com.kotliners.piedrapapeltijera.data.AppDatabase

class GameApplication : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}
