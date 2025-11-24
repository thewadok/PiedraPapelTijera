package com.kotliners.piedrapapeltijera.utils.media

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.kotliners.piedrapapeltijera.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

@Suppress("DEPRECATION")
class MusicService : Service() {

    companion object {
        @Volatile
        var isRunning: Boolean = false
    }

    // Con esta funcion podremos acceder al servicio desde la Activity
    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    private val binder = LocalBinder()

    // MediaPlayer va ha  reproducir la música de fondo
    private var mediaPlayer: MediaPlayer? = null

    // Scope para poder ejecutar las cosas en segundo plano (cumpliendo con la concurrencia)
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    //Gestor de audio en el sistema para llamadas, otras apps ...
    private lateinit var audioManager: AudioManager

    //Para saber si debemos reanudar al recuperar el foco
    private var shouldResumeAfterFocusGain = false

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        //Inicializo el AudioManager para el sistema de audio
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        // Inicializamos el MediaPlayer en segundo plano
        serviceScope.launch {
            initPlayer()
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Cuando se arranca el servicio, empezamos la música
        playMusic()
        // Indicamos al sistema que intente mantener el servicio funcionando
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        // Liberamos recursos al destruir el servicio
        mediaPlayer?.release()
        mediaPlayer = null
        abandonAudioFocus()
        serviceScope.cancel()
    }

    // Leemos de preferencias qué pista quiere el usuario
    private fun getSelectedTrackResId(): Int? {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val trackKey = prefs.getString("music_track", "fondo")

        return when (trackKey) {
            "fondo"  -> R.raw.fondo
            "fondo2" -> R.raw.fondo2
            "fondo3" -> R.raw.fondo3
            "mute"   -> null            // Silenciado: sin música
            else     -> R.raw.fondo     // Por si acaso
        }
    }

    // Inicializo el MediaPlayer con la música de fondo que tengo en raw
    private fun initPlayer() {
        // Por si acaso ya existía un MediaPlayer, lo liberamos
        mediaPlayer?.release()
        mediaPlayer = null

        val resId = getSelectedTrackResId() ?: return

        // Si el usuario ha elegido "silenciar", no creamos MediaPlayer

        mediaPlayer = MediaPlayer.create(this, resId).apply {
            isLooping = true   // que suene en bucle
            setVolume(1f, 1f)
        }
    }

    // Empiezo a reproducir la musica (si el MediaPlayer está listo)
    fun playMusic() {
        serviceScope.launch {
            val resId = getSelectedTrackResId()

            // Si está en modo silencio, no hacemos nada
            if (resId == null) {
                mediaPlayer?.pause()
                mediaPlayer?.seekTo(0)
                mediaPlayer = null
                abandonAudioFocus()
                return@launch
            }

            if (mediaPlayer == null) {
                initPlayer()
            }
            // Pido el foco de audio al sistema para llamadas, apps..
            if (requestAudioFocus()) {
                if (mediaPlayer?.isPlaying == false) {
                    mediaPlayer?.start()
                }
            }
        }
    }


    //Paro la musica si esta sonando
    fun pauseMusic() {
        serviceScope.launch {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
            // Si la paramos desde la app, soltamos el foco de audio
            abandonAudioFocus()
        }
    }

    //Devuelve true si la musica esta sonando
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    // Uso de las funciones que piden de focus
    // Listener para poder reaccionar a las apps que necesitan usar audio como alarmas, llamadas... de audio
    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // Perdemos el foco un momento (por ejemplo, notificación)
                if (mediaPlayer?.isPlaying == true) {
                    shouldResumeAfterFocusGain = true
                    mediaPlayer?.pause()
                }
            }

            AudioManager.AUDIOFOCUS_LOSS -> {
                // Otra app se queda con el audio.
                // Nosotros paramos, pero queremos reanudar si luego recuperamos el foco.
                if (mediaPlayer?.isPlaying == true) {
                    shouldResumeAfterFocusGain = true
                    mediaPlayer?.pause()
                }
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // Podemos "bajar volumen" en vez de parar
                mediaPlayer?.setVolume(0.2f, 0.2f)
            }

            AudioManager.AUDIOFOCUS_GAIN -> {
                // Recuperamos el foco: subimos volumen y reanudamos si toca
                mediaPlayer?.setVolume(1f, 1f)
                if (shouldResumeAfterFocusGain) {
                    shouldResumeAfterFocusGain = false
                    mediaPlayer?.start()
                }
            }
        }
    }

    // Pedimos foco de audio al sistema
    private fun requestAudioFocus(): Boolean {
        val result = audioManager.requestAudioFocus(
            audioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    // Soltamos el foco de audio cuando ya no necesitamos sonar
    private fun abandonAudioFocus() {
        audioManager.abandonAudioFocus(audioFocusChangeListener)
    }
}

