package com.kotliners.piedrapapeltijera

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.kotliners.piedrapapeltijera.ui.AppRoot
import com.kotliners.piedrapapeltijera.ui.theme.FondoNegro
import com.kotliners.piedrapapeltijera.utils.locale.LocaleManager
import com.kotliners.piedrapapeltijera.utils.notifications.NotificationsPermission
import com.kotliners.piedrapapeltijera.utils.media.MusicService
import com.kotliners.piedrapapeltijera.utils.media.SoundEffects
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.kotliners.piedrapapeltijera.ui.auth.AuthManager
import com.kotliners.piedrapapeltijera.ui.screens.LoginScreen
import kotlinx.coroutines.launch

/**
 * Activity principal combinada:
 Localización aplicada al contexto base
 Música y efectos del equipo
 Permisos de calendario
 Permiso de notificaciones
 Duración de la victoria
 */
class MainActivity : ComponentActivity() {

    // Variable que recoge las claves de autenticación de Firebase
    private val authManager by lazy { AuthManager(this) }

    // Localización antes de crear la actividad
    override fun attachBaseContext(newBase: Context) {
        val context = LocaleManager.applySavedLocale(newBase)
        super.attachBaseContext(context)
    }

    // Permisos de calendario
    private val requestCalendarPerms = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    // Control de música
    fun toggleMusic() {
        if (MusicService.isRunning) {
            stopService(Intent(this, MusicService::class.java))
        } else {
            startService(Intent(this, MusicService::class.java))
        }
    }

    fun isMusicRunning() = MusicService.isRunning

    // onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pedir permisos del calendario
        requestCalendarPerms.launch(
            arrayOf(
                android.Manifest.permission.READ_CALENDAR,
                android.Manifest.permission.WRITE_CALENDAR
            )
        )

        // Iniciar música y efectos
        startService(Intent(this, MusicService::class.java))
        SoundEffects.init(applicationContext)

        // Configuración visual
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(FondoNegro.value.toInt()),
            navigationBarStyle = SystemBarStyle.dark(FondoNegro.value.toInt())
        )

        // Cargar Compose
        setContent {
            // 1. Creamos una variable de estado para saber quién es el usuario actual.
            //    Inicialmente, comprobamos si ya hay alguien autenticado.
            var user by remember { mutableStateOf(authManager.getCurrentUser()) }

            // 2. Creamos el "lanzador" que gestionará el resultado del inicio de sesión de Google.
            val signInLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                // Cuando Google nos devuelve un resultado, lo procesamos.
                lifecycleScope.launch {
                    val signedInUser = authManager.handleSignInResult(result.data)
                    if (signedInUser != null) {
                        // Si el inicio de sesión fue exitoso, actualizamos el estado.
                        // Esto hará que la interfaz se recomponga y muestre AppRoot.
                        user = signedInUser
                    } else {
                        // Si falló, mostramos un mensaje al usuario.
                        Toast.makeText(this@MainActivity, "Inicio de sesión fallido", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // 3. Lógica principal de la UI:
            //    Si el usuario es nulo (nadie ha iniciado sesión), mostramos la LoginScreen.
            //    Si no es nulo, mostramos la aplicación principal (AppRoot).
            if (user == null) {
                LoginScreen(
                    onSignInClick = {
                        // Cuando el usuario pulsa el botón, lanzamos el flujo de inicio de sesión.
                        val signInIntent = authManager.getSignInIntent()
                        signInLauncher.launch(signInIntent)
                    }
                )
            } else {
                // El usuario ya está autenticado, mostramos la app principal.
                // Más adelante, pasaremos aquí una función para cerrar sesión.
                val navController = rememberNavController()
                AppRoot(user = user!!, navController = navController)
            }
        }

        // Permiso de notificaciones
        NotificationsPermission.requestIfNeeded(this)

        // Procesar posible tiempo recibido desde una notificación
        handleNotificationIntent()
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundEffects.release()
    }

    private fun handleNotificationIntent() {
        val time = intent.getStringExtra("EXTRA_TIME")
        if (!time.isNullOrEmpty()) {
            Toast.makeText(this, "Tiempo de resolución: $time", Toast.LENGTH_LONG).show()
        }
    }
}