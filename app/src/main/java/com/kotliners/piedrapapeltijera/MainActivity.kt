package com.kotliners.piedrapapeltijera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.activity.viewModels
import com.kotliners.piedrapapeltijera.ui.theme.*
import com.kotliners.piedrapapeltijera.ui.AppRoot
import com.kotliners.piedrapapeltijera.ui.viewmodel.MainViewModel
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
import com.kotliners.piedrapapeltijera.utils.media.MusicService


//Activity principal desde donde arrancamos
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    // Pedimos permisos de calendario
    private val requestCalendarPerms = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms[android.Manifest.permission.WRITE_CALENDAR] == true &&
                perms[android.Manifest.permission.READ_CALENDAR] == true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Solicitamos permisos al iniciar
        requestCalendarPerms.launch(
            arrayOf(
                android.Manifest.permission.READ_CALENDAR,
                android.Manifest.permission.WRITE_CALENDAR
            )
        )
        //Iniciamos la musica de fondo (Luego le pondre boton para poder apagarla durante la partida)
        startService(Intent(this, MusicService::class.java))


        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(FondoNegro.value.toInt()),
            navigationBarStyle = SystemBarStyle.dark(FondoNegro.value.toInt())
        )
        setContent {
            AppRoot()
        }
    }
}