package com.kotliners.piedrapapeltijera.utils.media

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.kotliners.piedrapapeltijera.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

class MusicService : Service() {

    // Con esta funcion podremos acceder al servicio desde la Activity
    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    private val binder = LocalBinder()

    // MediaPlayer va ha  reproducir la música de fondo
    private var mediaPlayer: MediaPlayer? = null

    // Scope para poder ejecutar las cosas en segundo plano (cumpliendo con la concurrencia)
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        // Inicializamos el MediaPlayer en segundo plano
        serviceScope.launch {
            initPlayer()
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Cuando se arranca el servicio, empezamos la música
        playMusic()

        // START_STICKY indica al sistema que intente mantener el servicio funcionando
        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        // Liberamos recursos al destruir el servicio
        mediaPlayer?.release()
        mediaPlayer = null
        serviceScope.cancel()
    }

    /** Inicializa el MediaPlayer con la música de fondo del raw */
    private fun initPlayer() {
        // Por si acaso ya existía un MediaPlayer, lo liberamos
        mediaPlayer?.release()

        mediaPlayer = MediaPlayer.create(this, R.raw.fondo).apply {
            isLooping = true   // que suene en bucle
        }
    }

    /** Empieza a reproducir la música (si el MediaPlayer está listo) */
    fun playMusic() {
        serviceScope.launch {
            if (mediaPlayer == null) {
                initPlayer()
            }
            if (mediaPlayer?.isPlaying == false) {
                mediaPlayer?.start()
            }
        }
    }

    /** Pausa la música si está sonando */
    fun pauseMusic() {
        serviceScope.launch {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        }
    }

    /** Devuelve true si la música está sonando */
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }
}
