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

    // Para salir del juego
    var showExitDialog by remember { mutableStateOf(false) }

    // Preferencias para música
    val prefs = remember {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    var selectedTrack by remember {
        mutableStateOf(prefs.getString("music_track", "fondo") ?: "fondo")
    }

    fun seleccionarMusica(trackKey: String) {
        selectedTrack = trackKey
        prefs.edit().putString("music_track", trackKey).apply()

        // Reiniciar MusicService
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
        TituloPrincipal(text = stringResource(R.string.settings_title))

        Spacer(Modifier.height(8.dp))


        // 🔥 RESET
        NeonTextoBoton(text = stringResource(R.string.reset_button)) {
            viewModel.resetJuego()
            nav.safeNavigate(Screen.Game.route)
        }

        Spacer(Modifier.height(8.dp))

        Parrafo(text = stringResource(R.string.reset_description))

        Spacer(Modifier.height(24.dp))


        // 🟢 RESCATE
        NeonTextoBoton(text = stringResource(R.string.rescue_button)) {
            viewModel.rescate()
            nav.safeNavigate(Screen.Game.route)
        }

        Spacer(Modifier.height(8.dp))

        Parrafo(text = stringResource(R.string.rescue_description))

        Spacer(Modifier.height(32.dp))


        // 🌍 Selector de idioma
        TituloPrincipal(text = stringResource(R.string.language_section_title))

        Spacer(Modifier.height(12.dp))

        Parrafo(text = stringResource(R.string.language_instruction))

        Spacer(Modifier.height(12.dp))

        // Botón Español
        Button(
            onClick = {
                if (selectedLang != "es") {
                    selectedLang = "es"
                    LocaleManager.updateActivityLocale(activity!!, "es")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.language_spanish))
        }

        Spacer(Modifier.height(8.dp))

        // Botón Inglés
        Button(
            onClick = {
                if (selectedLang != "en") {
                    selectedLang = "en"
                    LocaleManager.updateActivityLocale(activity!!, "en")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.language_english))
        }

        Spacer(Modifier.height(32.dp))


        // 🎵 Selector de música (develop)
        TituloPrincipal(text = stringResource(R.string.music_settings_title))

        Spacer(Modifier.height(8.dp))

        Parrafo(text = stringResource(R.string.music_settings_desc))

        Spacer(Modifier.height(12.dp))

        val opcionesMusica = listOf(
            "fondo" to stringResource(R.string.music_original),
            "fondo2" to stringResource(R.string.music_alt1),
            "fondo3" to stringResource(R.string.music_alt2),
            "mute" to stringResource(R.string.music_mute)
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

        Spacer(Modifier.height(32.dp))

        // 🔴 Salir del juego
        NeonTextoBoton(text = stringResource(R.string.exit_game)) {
            showExitDialog = true
        }

        Spacer(Modifier.height(24.dp))
    }

    // ❗ Diálogo para cerrar la app
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(stringResource(R.string.exit_game)) },
            text = { Text(stringResource(R.string.exit_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    activity?.finish()
                }) {
                    Text(stringResource(R.string.exit_game))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}