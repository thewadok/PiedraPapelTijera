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
import androidx.core.content.edit
import androidx.navigation.NavHostController
import com.kotliners.piedrapapeltijera.R
import com.kotliners.piedrapapeltijera.MainActivity
import com.kotliners.piedrapapeltijera.navigation.Screen
import com.kotliners.piedrapapeltijera.navigation.safeNavigate
import com.kotliners.piedrapapeltijera.ui.components.*
import com.kotliners.piedrapapeltijera.ui.theme.*
import com.kotliners.piedrapapeltijera.ui.viewmodel.MainViewModel
import com.kotliners.piedrapapeltijera.utils.locale.LocaleManager
import com.kotliners.piedrapapeltijera.utils.media.MusicService
import com.kotliners.piedrapapeltijera.utils.system.exitGame
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.kotliners.piedrapapeltijera.utils.auth.GoogleReauthAndDelete

@Composable
fun SettingScreen(
    nav: NavHostController,
    mainViewModel: MainViewModel
) {
    // Preparamos el scroll vertical de la pantalla
    val scroll = rememberScrollState()

    // Obtenemos el contexto y la actividad actual
    val context = LocalContext.current
    val activity = context as? Activity

    // Idioma guardado
    var selectedLang by remember { mutableStateOf(LocaleManager.getSavedLanguage(context)) }

    // Diálogo salir del juego
    var showExitDialog by remember { mutableStateOf(false) }

    // Diálogo cerrar sesión
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Diálogo eliminar cuenta
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    // Lanzar reauth y delete en Google
    var startReauthDelete by remember { mutableStateOf(false) }

    // Música
    val prefs = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

    // Guardamos la pista de música seleccionada
    var selectedTrack by remember {
        mutableStateOf(prefs.getString("music_track", "fondo") ?: "fondo")
    }

    // Cambiamos la música, guardamos la opción y reiniciamos el servicio
    fun seleccionarMusica(trackKey: String) {
        selectedTrack = trackKey

        prefs.edit {
            putString("music_track", trackKey)
        }

        val mainActivity = activity as? MainActivity ?: return

        // Siempre paramos el servicio primero para evitar cualquier solapamiento extraño
        mainActivity.stopService(Intent(mainActivity, MusicService::class.java))

        // Si no es "mute", arrancamos de nuevo el servicio con la pista nueva
        if (trackKey != "mute") {
            mainActivity.startService(Intent(mainActivity, MusicService::class.java))
        }
    }

    // Contenedor principal de la pantalla de ajustes
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoNegro)
            .verticalScroll(scroll)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {

        // Título principal
        TituloPrincipal(stringResource(R.string.settings_title))

        Spacer(Modifier.height(12.dp))

        // Reset
        NeonTextoBoton(stringResource(R.string.reset_button)) {
            mainViewModel.resetJuego()
            nav.safeNavigate(Screen.Game.route)
        }

        Spacer(Modifier.height(8.dp))

        Parrafo(stringResource(R.string.reset_description))

        Spacer(Modifier.height(24.dp))

        // Rescate
        NeonTextoBoton(stringResource(R.string.rescue_button)) {
            mainViewModel.rescate()
            nav.safeNavigate(Screen.Game.route)
        }

        Spacer(Modifier.height(8.dp))

        Parrafo(stringResource(R.string.rescue_description))

        Spacer(Modifier.height(24.dp))

        // Selección de idioma
        TituloSeccion(stringResource(R.string.language_section_title))

        Spacer(Modifier.height(8.dp))

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
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulNeon,
                contentColor = TextoNegro
            )
        ) {
            Text(stringResource(R.string.language_spanish),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
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
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulNeon,
                contentColor = TextoNegro
            )
        ) {
            Text(stringResource(R.string.language_english),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
                )
        }

        Spacer(Modifier.height(24.dp))

        // Música
        TituloSeccion(stringResource(R.string.music_section_title))

        Spacer(Modifier.height(8.dp))

        Parrafo(stringResource(R.string.music_section_description))

        Spacer(Modifier.height(8.dp))

        val opcionesMusica = listOf(
            "fondo" to R.string.music_option_original,
            "fondo2" to R.string.music_option_alt1,
            "fondo3" to R.string.music_option_alt2,
            "mute" to R.string.music_option_mute
        )

        opcionesMusica.forEach { (key, labelRes) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        seleccionarMusica(key)
                    }
                    .padding(vertical = 1.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedTrack == key,
                    onClick = {
                        seleccionarMusica(key)
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = AzulNeon,
                        unselectedColor = AzulNeon.copy(alpha = 0.6f)
                    )
                )
                Spacer(Modifier.width(4.dp))
                Text(stringResource(labelRes), color = TextoBlanco)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Sesión
        TituloPrincipal(stringResource(R.string.session_section_title))

        Spacer(Modifier.height(12.dp))

        // Salir del juego
        NeonTextoBoton(stringResource(R.string.exit_game)) {
            showExitDialog = true
        }

        Spacer(Modifier.height(8.dp))

        Parrafo(stringResource(R.string.exit_game_description))

        Spacer(Modifier.height(24.dp))

        // Cerrar sesión
        NeonTextoBoton(stringResource(R.string.logout)) {
            showLogoutDialog = true
        }

        Spacer(Modifier.height(8.dp))

        Parrafo(stringResource(R.string.logout_description))

        Spacer(Modifier.height(24.dp))

        TituloPrincipal(stringResource(R.string.danger_zone_title))

        Spacer(Modifier.height(12.dp))

        // Eliminar cuenta
        NeonTextoBoton(stringResource(R.string.delete_account_button)) {
            showDeleteAccountDialog = true
        }

        Spacer(Modifier.height(8.dp))
        Parrafo(stringResource(R.string.delete_account_description))

        Spacer(Modifier.height(24.dp))
    }

    // Diálogo de confirmación para salir del juego
    if (showExitDialog) {
        ExitGameDialog(
            title = stringResource(R.string.exit_game_title),
            message = stringResource(R.string.exit_game_message),
            confirmText = stringResource(R.string.exit_game_confirm),
            dismissText = stringResource(R.string.exit_game_cancel),
            onConfirmExit = {
                activity?.exitGame()
            },
            onDismiss = {
                showExitDialog = false
            }
        )
    }

    // Diálogo de confirmación para cerrar sesión
    if (showLogoutDialog) {
        ExitGameDialog(
            title = stringResource(R.string.logout_title),
            message = stringResource(R.string.logout_message),
            confirmText = stringResource(R.string.logout_confirm),
            dismissText = stringResource(R.string.logout_cancel),
            onConfirmExit = {
                showLogoutDialog = false

                // Paramos la música antes de cerrar
                activity?.startService(Intent(activity, MusicService::class.java).apply {
                    action = MusicService.ACTION_STOP
                })

                // Cerramos sesión Firebase
                FirebaseAuth.getInstance().signOut()

                // Cerramos la app completamente
                activity?.finishAffinity()
            },
            onDismiss = {
                showLogoutDialog = false
            }
        )
    }

    // Diálogo de confirmación para eliminar la cuenta
    if (showDeleteAccountDialog) {
        ExitGameDialog(
            title = stringResource(R.string.delete_account_title),
            message = stringResource(R.string.delete_account_message),
            confirmText = stringResource(R.string.delete_account_confirm),
            dismissText = stringResource(R.string.delete_account_cancel),
            onConfirmExit = {
                showDeleteAccountDialog = false

                // Intentamos borrar completo
                mainViewModel.eliminarCuentaCompleta(
                    onOk = {

                        // Paramos la música antes de cerrar
                        activity?.startService(Intent(activity, MusicService::class.java).apply {
                            action = MusicService.ACTION_STOP
                        })

                        // Cerramos app
                        activity?.finishAffinity()
                    },
                    onRequiresRecentLogin = {
                        startReauthDelete = true
                    },
                    onError = {

                        // Paramos la música antes de cerrar
                        activity?.startService(
                            Intent(activity, MusicService::class.java).apply {
                                action = MusicService.ACTION_STOP
                            }
                        )

                        activity?.finishAffinity()
                    }
                )
            },
            onDismiss = { showDeleteAccountDialog = false }
        )
    }

    // Gestionamos la reautenticación de Google si es necesaria
    if (startReauthDelete) {
        GoogleReauthAndDelete(
            activity = activity,
            context = context,
            mainViewModel = mainViewModel,
            onFinish = { startReauthDelete = false }
        )
    }
}