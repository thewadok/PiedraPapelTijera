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

        // TEST: leer datos de Firebase con Retrofit + Moshi
        lifecycleScope.launch {
            try {
                // Llamadas a la API
                val jugadoresMap = RetrofitInstance.api.getJugadores()
                val partidasMap = RetrofitInstance.api.getPartidas()
                val premio = RetrofitInstance.api.getPremioComun()

                // Convertimos los Map a List (solo nos interesan los valores)
                val jugadoresLista = jugadoresMap.values.toList()
                val partidasLista = partidasMap.values.toList()

                val sb = StringBuilder()

                // ---- JUGADORES ----
                sb.append("JUGADORES:\n")
                for (jugador in jugadoresLista) {
                    val uid = jugador.uid ?: "(sin uid)"
                    val nombre = jugador.nombre ?: "Desconocido"
                    val monedas = jugador.monedas ?: 0
                    val victorias = jugador.victorias ?: 0
                    val derrotas = jugador.derrotas ?: 0

                    sb.append("uid=$uid | nombre=$nombre | monedas=$monedas | victorias=$victorias | derrotas=$derrotas\n")
                }

                // ---- PARTIDAS ----
                sb.append("\nPARTIDAS:\n")
                for (partida in partidasLista) {
                    val uidPartida = partida.uid ?: "(sin uid)"
                    val fecha = partida.fecha ?: 0L
                    val jugadaJugador = partida.jugadaJugador ?: "?"
                    val jugadaCpu = partida.jugadaCpu ?: "?"
                    val resultado = partida.resultado ?: "desconocido"
                    val apuesta = partida.apuesta ?: 0
                    val cambioMonedas = partida.cambioMonedas ?: 0
                    val latitud = partida.latitud ?: 0.0
                    val longitud = partida.longitud ?: 0.0

                    sb.append(
                        "uid=$uidPartida | fecha=$fecha | jugadaJugador=$jugadaJugador | " +
                                "jugadaCpu=$jugadaCpu | resultado=$resultado | apuesta=$apuesta | " +
                                "cambioMonedas=$cambioMonedas | lat=$latitud | lon=$longitud\n"
                    )
                }

                // ---- PREMIO COMÚN ----
                sb.append("\nPREMIO COMÚN:\n")
                val monedasEnBote = premio.monedasEnBote ?: 0
                val ultimoGanadorUid = premio.ultimoGanadorUid ?: "(ninguno)"
                sb.append("monedasEnBote=$monedasEnBote | ultimoGanadorUid=$ultimoGanadorUid\n")

                android.util.Log.d("FirebaseRetrofitTest", sb.toString())

            } catch (e: Exception) {
                android.util.Log.e("FirebaseRetrofitTest", "ERROR al llamar a Firebase: ${e.message}", e)
            }
        }

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