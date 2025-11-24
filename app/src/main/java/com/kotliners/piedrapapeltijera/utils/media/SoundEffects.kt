package com.kotliners.piedrapapeltijera.utils.media

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

import com.kotliners.piedrapapeltijera.R

// Gestor de sonidos cortos (clicks, victoria, derrota)
object SoundEffects {

    private var soundPool: SoundPool? = null

    // Ids de los sonidos dentro del SoundPool
    private var clickId: Int = 0
    private var winId: Int = 0
    private var loseId: Int = 0

    // Para saber si ya se han cargado los sonidos
    private var loaded = false

    // Llamar solo una vez al iniciar la app (por ejemplo en MainActivity)
    fun init(context: Context) {
        if (soundPool != null) return  // ya está inicializado

        soundPool = SoundPool.Builder()
            .setMaxStreams(3) // hasta 3 sonidos a la vez
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build().apply {
                setOnLoadCompleteListener { _, _, _ ->
                    loaded = true
                }
            }

        // Cargamos los sonidos desde res/raw
        soundPool?.let { sp ->
            clickId = sp.load(context, R.raw.boton_corto, 1)
            winId   = sp.load(context, R.raw.victoria, 1)
            loseId  = sp.load(context, R.raw.perdiste, 1)
        }
    }

    // Sonido para clicks de botones
    fun playClick() {
        if (loaded) {
            soundPool?.play(clickId, 1f, 1f, 1, 0, 1f)
        }
    }

    // Sonido para victoria
    fun playWin() {
        if (loaded) {
            soundPool?.play(winId, 1f, 1f, 1, 0, 1f)
        }
    }

    // Sonido para derrota
    fun playLose() {
        if (loaded) {
            soundPool?.play(loseId, 1f, 1f, 1, 0, 1f)
        }
    }

    //  limpiar recursos
    fun release() {
        soundPool?.release()
        soundPool = null
        loaded = false
    }
}
