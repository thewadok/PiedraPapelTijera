package com.kotliners.piedrapapeltijera.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kotliners.piedrapapeltijera.R
import com.kotliners.piedrapapeltijera.MainActivity
import com.kotliners.piedrapapeltijera.navigation.Screen
import com.kotliners.piedrapapeltijera.navigation.safeNavigate
import com.kotliners.piedrapapeltijera.ui.components.*
import com.kotliners.piedrapapeltijera.ui.theme.*
import com.kotliners.piedrapapeltijera.ui.viewmodel.MainViewModel
import com.kotliners.piedrapapeltijera.utils.LocaleManager
import com.kotliners.piedrapapeltijera.utils.media.MusicService

@Composable
fun SettingScreen(
    nav: NavHostController,
    viewModel: MainViewModel = viewModel()
) {
    val scroll = rememberScrollState()
    val context = LocalContext.current
    val activity = context as? Activity

    // Idioma guardado
    var selectedLang by remember { mutableStateOf(LocaleManager.getSavedLanguage(context)) }

    // Diálogo salir del juego
    var showExitDialog by remember { mutableStateOf(false) }

    // Música (shared preferences)
    val prefs = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

    var selectedTrack by remember {
        mutableStateOf(prefs.getString("music_track", "fondo") ?: "fondo")
    }

    fun seleccionarMusica(trackKey: String) {
        selectedTrack = trackKey
        prefs.edit().putString("music_track", trackKey).apply()

        (activity as? MainActivity)?.let {
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

        // ⭐ Título principal
        TituloPrincipal(stringResource(R.string.settings_title))

        Spacer(Modifier.height(8.dp))

        // 🔥 RESET
        NeonTextoBoton(stringResource(R.string.reset_button)) {
            viewModel.resetJuego()
            nav.safeNavigate(Screen.Game.route)
        }

        Spacer(Modifier.height(8.dp))

        Parrafo(stringResource(R.string.reset_description))

        Spacer(Modifier.height(24.dp))

        // 🟢 RESCATE
        NeonTextoBoton(stringResource(R.string.rescue_button)) {
            viewModel.rescate()
            nav.safeNavigate(Screen.Game.route)
        }

        Spacer(Modifier.height(8.dp))

        Parrafo(stringResource(R.string.rescue_description))

        Spacer(Modifier.height(32.dp))

        // 🌍 Selección de idioma
        TituloPrincipal(stringResource(R.string.language_section_title))

        Spacer(Modifier.height(12.dp))

        Parrafo(stringResource(R.string.language_instruction))

        Spacer(Modifier.height(12.dp))

        // Español
        Button(
            onClick = {
                if (selectedLang != "es") {
                    selectedLang = "es"
                    LocaleManager.updateActivityLocale(activity!!, "es")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.language_spanish))
        }

        Spacer(Modifier.height(8.dp))

        // Inglés
        Button(
            onClick = {
                if (selectedLang != "en") {
                    selectedLang = "en"
                    LocaleManager.updateActivityLocale(activity!!, "en")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.language_english))
        }

        Spacer(Modifier.height(32.dp))

        // 🎵 Música (ADD → develop)
        TituloPrincipal("Música de fondo")

        Spacer(Modifier.height(8.dp))

        Parrafo("Selecciona la melodía de fondo o silencia la música.")

        Spacer(Modifier.height(12.dp))

        val opcionesMusica = listOf(
            "fondo" to "Música original",
            "fondo2" to "Música alternativa 1",
            "fondo3" to "Música alternativa 2",
            "mute" to "Silenciar música"
        )

        opcionesMusica.forEach { (key, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { seleccionarMusica(key) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedTrack == key,
                    onClick = { seleccionarMusica(key) }
                )
                Spacer(Modifier.width(8.dp))
                Text(text = label, color = TextoBlanco)
            }
        }

        Spacer(Modifier.height(40.dp))

        // 🔴 Salir del juego
        NeonTextoBoton("Salir del juego") {
            showExitDialog = true
        }

        Spacer(Modifier.height(24.dp))
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Salir del juego") },
            text = { Text("¿Seguro que quieres cerrar la aplicación?") },
            confirmButton = {
                TextButton(onClick = {
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