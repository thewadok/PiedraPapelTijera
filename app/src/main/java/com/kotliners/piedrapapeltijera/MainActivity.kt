package com.kotliners.piedrapapeltijera

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.activity.viewModels
import com.kotliners.piedrapapeltijera.ui.theme.*
import com.kotliners.piedrapapeltijera.ui.AppRoot
import com.kotliners.piedrapapeltijera.ui.viewmodel.MainViewModel
import com.kotliners.piedrapapeltijera.utils.NotificationsPermission
import com.kotliners.piedrapapeltijera.utils.LocaleManager

/**
 * Activity principal.
 * - Aplica el idioma guardado antes de inicializar Compose.
 * - Gestiona el permiso de notificaciones.
 * - Muestra la duración desde la notificación.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    // ✅ Se aplica el idioma guardado ANTES de crear la Activity
    override fun attachBaseContext(newBase: Context) {
        val context = LocaleManager.applySavedLocale(newBase)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(FondoNegro.value.toInt()),
            navigationBarStyle = SystemBarStyle.dark(FondoNegro.value.toInt())
        )

        setContent {
            AppRoot()
        }

        // ✅ Pedir permiso de notificaciones (solo Android 13+)
        NotificationsPermission.requestIfNeeded(this)

        // ✅ Mostrar tiempo si viene desde la notificación
        handleNotificationIntent()
    }

    private fun handleNotificationIntent() {
        val time = intent.getStringExtra("EXTRA_TIME")
        if (!time.isNullOrEmpty()) {
            Toast.makeText(
                this,
                "⏱ Tiempo de resolución: $time",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}