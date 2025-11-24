package com.kotliners.piedrapapeltijera.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kotliners.piedrapapeltijera.MainActivity
import com.kotliners.piedrapapeltijera.navigation.Screen
import com.kotliners.piedrapapeltijera.navigation.safeNavigate
import com.kotliners.piedrapapeltijera.ui.components.NeonTextoBoton
import com.kotliners.piedrapapeltijera.ui.components.Parrafo
import com.kotliners.piedrapapeltijera.ui.components.TituloPrincipal
import com.kotliners.piedrapapeltijera.ui.theme.FondoNegro
import com.kotliners.piedrapapeltijera.ui.theme.TextoBlanco
import com.kotliners.piedrapapeltijera.ui.viewmodel.MainViewModel
import com.kotliners.piedrapapeltijera.utils.media.MusicService

// Pantalla de Ajustes
@Composable
fun SettingScreen(
    nav: NavHostController,
    viewModel: MainViewModel = viewModel()
) {
    val scroll = rememberScrollState()
    val context = LocalContext.current
    val activity = context as? MainActivity

    // para el diálogo de salir del juego
    var showExitDialog by remember { mutableStateOf(false) }

    // Preferencias donde guardamos que musica va a usar el juego
    val prefs = remember {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    // Leemos la música guardada, por defecto es "fondo"
    var selectedTrack by remember {
        mutableStateOf(prefs.getString("music_track", "fondo") ?: "fondo")
    }

    // Función para cambiar de música y reiniciar el servicio
    fun seleccionarMusica(trackKey: String) {
        selectedTrack = trackKey
        prefs.edit().putString("music_track", trackKey).apply()

        // Reiniciamos el MusicService para que cargue la nueva melodía
        activity?.let {
            it.stopService(Intent(it, MusicService::class.java))
            it.startService(Intent(it, MusicService::class.java))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoNegro)
            .verticalScroll(scroll)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {

        TituloPrincipal("Ajustes de configuración")

        Spacer(Modifier.height(8.dp))

        // RESET
        NeonTextoBoton("Reset") {
            viewModel.resetJuego()
            nav.safeNavigate(Screen.Game.route)
        }

        Spacer(Modifier.height(8.dp))

        Parrafo(
            "Toca Reset para restablecer tu estado al estado inicial."
        )

        Spacer(Modifier.height(24.dp))

        // RESCATE
        NeonTextoBoton("Rescate") {
            viewModel.rescate()
            nav.safeNavigate(Screen.Game.route)
        }

        Spacer(Modifier.height(8.dp))

        Parrafo(
            "Toca Rescate y compra 50 monedas extra para continuar."
        )

        Spacer(Modifier.height(24.dp))

        // AJUSTES DE MÚSICA
        TituloPrincipal("Música de fondo")

        Spacer(Modifier.height(8.dp))

        Parrafo(
            "Selecciona la melodía de fondo o silencia la música del juego durante la partida."
        )

        Spacer(Modifier.height(12.dp))

        // Lista de opciones de música disponibles
        val opcionesMusica = listOf(
            "fondo"  to "Música original",
            "fondo2" to "Música alternativa 1",
            "fondo3" to "Música alternativa 2",
            "mute"   to "Silenciar música de fondo"
        )

        opcionesMusica.forEach { (key, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Cuando se toca la fila, cambiamos la música
                        seleccionarMusica(key)
                    }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (selectedTrack == key),
                    onClick = {
                        seleccionarMusica(key)
                    }
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = label,
                    color = TextoBlanco
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Botón para salir del juego desde ajustes
        NeonTextoBoton("Salir del juego") {
            showExitDialog = true
        }

        Spacer(Modifier.height(24.dp))
    }

    // Diálogo de confirmación para salir del juego
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Salir del juego") },
            text = { Text("¿Seguro que quieres cerrar la aplicación?") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    activity?.finish()
                }) {
                    Text("Salir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
