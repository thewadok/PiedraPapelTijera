package com.kotliners.piedrapapeltijera.utils.media

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
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

        // Definimos las acciones disponibles para controlar la música
        const val ACTION_PLAY = "MUSIC_PLAY"
        const val ACTION_PAUSE = "MUSIC_PAUSE"
        const val ACTION_STOP = "MUSIC_STOP"
    }

    // MediaPlayer va ha  reproducir la música de fondo
    private var mediaPlayer: MediaPlayer? = null

    // Guardamos qué pista está cargada ahora mismo
    private var currentTrackResId: Int? = null

    // Scope para poder ejecutar las cosas en segundo plano (cumpliendo con la concurrencia)
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    //Gestor de audio en el sistema para llamadas, otras apps ...
    private lateinit var audioManager: AudioManager

    //Para saber si debemos reanudar al recuperar el foco
    private var shouldResumeAfterFocusGain = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        //Inicializo el AudioManager para el sistema de audio
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Indicamos que el servicio está en ejecución
        isRunning = true

        // Comprobamos la acción recibida en el Intent
        when (intent?.action) {
            // Reproducimos la música
            ACTION_PLAY -> playMusic()

            // Pausamos la música
            ACTION_PAUSE -> pauseMusic()

            // Detenemos la música y el servicio
            ACTION_STOP -> stopMusic()

            // Si no hay acción, reproducimos la música por defecto
            else -> playMusic()
        }

        // Mantenemos el servicio activo aunque el sistema lo cierre
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        pauseMusic()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false

        // Nos aseguramos de liberar bien el servicio para corregir el error de que siga sonando
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentTrackResId = null
        abandonAudioFocus()
        serviceScope.cancel()
    }

    // Leemos de settings qué pista quiere el usuario
    private fun getSelectedTrackResId(): Int? {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val trackKey = prefs.getString("music_track", "fondo")

        return when (trackKey) {
            "fondo"  -> R.raw.fondo
            "fondo2" -> R.raw.fondo2
            "fondo3" -> R.raw.fondo3
            "mute"   -> null
            else     -> R.raw.fondo
        }
    }

    // Inicializo el MediaPlayer con la música de fondo que tengo en raw
    private fun initPlayer(resId: Int) {
        // Por si acaso ya existía un MediaPlayer, lo liberamos
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        // Si el usuario ha elegido "silenciar", no creamos MediaPlayer
        mediaPlayer = MediaPlayer.create(this, resId).apply {
            isLooping = true
            setVolume(1f, 1f)
        }

        // Guardamos la pista que suena
        currentTrackResId = resId
    }

    // Empiezo a reproducir la musica (si el MediaPlayer está listo)
    fun playMusic() {
        serviceScope.launch {
            val resId = getSelectedTrackResId()
            if (resId == null) {
                stopMusic()
                return@launch
            }

            // Si la pista ha cambiado o no hay player, recreamos el MediaPlayer
            if (mediaPlayer == null || currentTrackResId != resId) {
                if (!requestAudioFocus()) {
                    return@launch
                }
                initPlayer(resId)
            }

            if (mediaPlayer?.isPlaying != true) {
                mediaPlayer?.start()
            }
        }
    }
    fun pauseMusic() {
        serviceScope.launch {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                abandonAudioFocus()
            }
        }
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentTrackResId = null
        abandonAudioFocus()
        isRunning = false
        stopSelf()
    }

    //Devuelve true si la musica esta sonando
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    // Listener para los cambios de foco de audio (llamadas, alarmas, YouTube, etc.)
    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        serviceScope.launch {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT, AudioManager.AUDIOFOCUS_LOSS -> {
                    if (mediaPlayer?.isPlaying == true) {
                        shouldResumeAfterFocusGain = true
                        mediaPlayer?.pause()
                    }
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    mediaPlayer?.setVolume(0.2f, 0.2f)
                }
                AudioManager.AUDIOFOCUS_GAIN -> {
                    mediaPlayer?.setVolume(1f, 1f)
                    if (shouldResumeAfterFocusGain) {
                        shouldResumeAfterFocusGain = false
                        mediaPlayer?.start()
                    }
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

