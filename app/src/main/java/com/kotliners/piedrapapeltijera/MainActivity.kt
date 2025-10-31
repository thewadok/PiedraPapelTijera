package com.kotliners.piedrapapeltijera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.util.Log

import com.kotliners.piedrapapeltijera.ui.theme.*
import com.kotliners.piedrapapeltijera.ui.AppRoot


import com.kotliners.piedrapapeltijera.data.AppDatabase
import com.kotliners.piedrapapeltijera.data.Jugador

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(FondoNegro.value.toInt()),
            navigationBarStyle = SystemBarStyle.dark(FondoNegro.value.toInt())
        )

        // ---- Inicialización de la interfaz Compose ----
        setContent {
            AppRoot()
        }

        // ---- Inicialización de la base de datos ----
        val app = application as GameApplication
        val db: AppDatabase = app.database
        val dao = db.gameDao()

        // ---- Insertar jugadora "Cristina" si no existe ----
        lifecycleScope.launch {
            val nombre = "Cristina"
            val existente = dao.getJugadorPorNombre(nombre)

            if (existente == null) {
                dao.insertarJugador(
                    Jugador(nombre_usuario = nombre)
                )
                Log.d("DB_TEST", "Jugador '$nombre' insertado en la base de datos.")
            } else {
                Log.d("DB_TEST", "Jugador '$nombre' ya existe con id=${existente.id_jugador}.")
            }
        }
    }
}
