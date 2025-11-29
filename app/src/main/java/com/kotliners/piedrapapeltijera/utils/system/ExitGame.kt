package com.kotliners.piedrapapeltijera.utils.system

import android.app.Activity
import android.content.Intent
import com.kotliners.piedrapapeltijera.MainActivity
import com.kotliners.piedrapapeltijera.utils.media.MusicService

fun Activity.exitGame() {

    // Detenemos la musica
    (this as? MainActivity)?.stopService(
        Intent(this, MusicService::class.java)
    )

    // Cerramos la Activity
    finish()
}