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
import com.kotliners.piedrapapeltijera.ui.AppRoot
import com.kotliners.piedrapapeltijera.ui.theme.FondoNegro
import com.kotliners.piedrapapeltijera.utils.locale.LocaleManager
import com.kotliners.piedrapapeltijera.utils.notifications.NotificationsPermission
import com.kotliners.piedrapapeltijera.utils.media.MusicService
import com.kotliners.piedrapapeltijera.utils.media.SoundEffects
import androidx.lifecycle.lifecycleScope
import com.kotliners.piedrapapeltijera.data.remote.firebase.RetrofitInstance
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

    //Creo una instancia del Firebase para poder conectar
    private val firebaseRepo =
        com.kotliners.piedrapapeltijera.data.repository.remote.FirebaseGameRepository()

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
/* Dejo este codigo comentado porque ya he verificado que sí conecta de forma adecuada*/

        /** // TEST COMPLETO: jugadores, top 10, partidas y premio común
      lifecycleScope.launch {
          try {
              // 1) Todos los jugadores (mapa uid -> JugadorRemoto)
              val jugadoresMap = firebaseRepo.fetchJugadores()

              // 2) Top 10 jugadores ordenados por victorias
              val topJugadores = firebaseRepo.fetchTopJugadores(limit = 10)

              // 3) Todas las partidas remotas
              val partidasMap = firebaseRepo.fetchPartidas()

              // 4) Premio común
              val premio = firebaseRepo.fetchPremioComun()

              val sb = StringBuilder()

              sb.append("=== JUGADORES (MAPA) ===\n")
              for ((uid, jugador) in jugadoresMap) {
                  sb.append("uid=$uid | nombre=${jugador.nombre} | monedas=${jugador.monedas} | victorias=${jugador.victorias} | derrotas=${jugador.derrotas}\n")
              }

              sb.append("\n=== TOP JUGADORES (ORDENADOS POR VICTORIAS) ===\n")
              topJugadores.forEachIndexed { index, jugador ->
                  sb.append("${index + 1}. nombre=${jugador.nombre} | victorias=${jugador.victorias} | monedas=${jugador.monedas}\n")
              }

              sb.append("\n=== PARTIDAS ===\n")
              for ((idPartida, partida) in partidasMap) {
                  sb.append(
                      "id=$idPartida | uid=${partida.uid} | resultado=${partida.resultado} | " +
                      "jugadaJugador=${partida.jugadaJugador} | jugadaCpu=${partida.jugadaCpu} | apuesta=${partida.apuesta} | cambioMonedas=${partida.cambioMonedas}\n"
                  )
              }

              sb.append("\n=== PREMIO COMÚN ===\n")
              sb.append("monedasEnBote=${premio.monedasEnBote} | ultimoGanadorUid=${premio.ultimoGanadorUid}\n")

              android.util.Log.d("FirebaseFullTest", sb.toString())

          } catch (e: Exception) {
              android.util.Log.e("FirebaseFullTest", "ERROR en test completo: ${e.message}", e)
          }
      }*/

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

        // onfiguración visual
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(FondoNegro.value.toInt()),
            navigationBarStyle = SystemBarStyle.dark(FondoNegro.value.toInt())
        )

        // Cargar Compose
        setContent {
            AppRoot()
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