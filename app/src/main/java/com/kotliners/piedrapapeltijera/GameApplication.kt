package com.kotliners.piedrapapeltijera
import android.app.Application
import data.AppDatabase

class GameApplication : Application() {

    val database: AppDatabase by lazy {        // 'lazy' crea la base de datos
                                               // la primera vez que alguien intente acceder a ella.
        AppDatabase.getDatabase(this) // Llama al 'Singleton' creado en AppDatabase.

    }

}
