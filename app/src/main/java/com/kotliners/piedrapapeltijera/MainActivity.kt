package com.kotliners.piedrapapeltijera

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.kotliners.piedrapapeltijera.ui.AppRoot
import com.kotliners.piedrapapeltijera.ui.theme.FondoNegro
import com.kotliners.piedrapapeltijera.ui.viewmodel.MainViewModel
import com.kotliners.piedrapapeltijera.utils.LocaleManager
import com.kotliners.piedrapapeltijera.utils.NotificationsPermission
import com.kotliners.piedrapapeltijera.utils.media.MusicService
import com.kotliners.piedrapapeltijera.utils.media.SoundEffects

/**
 * Activity principal combinada (Jose + develop)
 * - Localización aplicada al contexto base
 * - Música y efectos del equipo
 * - Permisos de calendario
 * - Permiso de notificaciones
 * - Toast con la duración de la victoria
 */
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    // ---------------------------------------------------------
    // 🔵 LOCALIZACIÓN ANTES DE CREAR LA ACTIVITY (TU PARTE)
    // ---------------------------------------------------------
    override fun attachBaseContext(newBase: Context) {
        val context = LocaleManager.applySavedLocale(newBase)
        super.attachBaseContext(context)
    }

    // ---------------------------------------------------------
    // 🔵 PERMISOS DEL CALENDARIO (DEVELOP)
    // ---------------------------------------------------------
    private val requestCalendarPerms = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* No necesitamos manejar nada aquí */ }

    // ---------------------------------------------------------
    // 🔵 CONTROL DE MÚSICA (DEVELOP)
    // ---------------------------------------------------------
    fun toggleMusic() {
        if (MusicService.isRunning) {
            stopService(Intent(this, MusicService::class.java))
        } else {
            startService(Intent(this, MusicService::class.java))
        }
    }

    fun isMusicRunning() = MusicService.isRunning

    // ---------------------------------------------------------
    // 🔵 onCreate FINAL — FUSION COMPLETA
    // ---------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // → Pedir permisos del calendario
        requestCalendarPerms.launch(
            arrayOf(
                android.Manifest.permission.READ_CALENDAR,
                android.Manifest.permission.WRITE_CALENDAR
            )
        )

        // → Iniciar música y efectos
        startService(Intent(this, MusicService::class.java))
        SoundEffects.init(applicationContext)

        // → Configuración visual
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(FondoNegro.value.toInt()),
            navigationBarStyle = SystemBarStyle.dark(FondoNegro.value.toInt())
        )

        // → Cargar Compose
        setContent {
            AppRoot()
        }

        // → Permiso de notificaciones (Android 13+)
        NotificationsPermission.requestIfNeeded(this)

        // → Procesar posible tiempo recibido desde una notificación
        handleNotificationIntent()
    }

    private fun handleNotificationIntent() {
        val time = intent.getStringExtra("EXTRA_TIME")
        if (!time.isNullOrEmpty()) {
            Toast.makeText(this, "⏱ Tiempo de resolución: $time", Toast.LENGTH_LONG).show()
        }
    }
}