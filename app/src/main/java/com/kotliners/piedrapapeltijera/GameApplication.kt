package com.kotliners.piedrapapeltijera
<<<<<<< HEAD
=======

>>>>>>> main
import android.app.Application
import com.kotliners.piedrapapeltijera.data.AppDatabase

class GameApplication : Application() {

    val database: AppDatabase by lazy {        // 'lazy' crea la base de datos
<<<<<<< HEAD
                                               // la primera vez que alguien intente acceder a ella.
=======
        // la primera vez que alguien intente acceder a ella.
>>>>>>> main
        AppDatabase.getDatabase(this) // Llama al 'Singleton' creado en AppDatabase.

    }

<<<<<<< HEAD
}
=======
}
>>>>>>> main
