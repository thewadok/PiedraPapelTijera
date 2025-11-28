package com.kotliners.piedrapapeltijera.utils.victory

import android.content.Context
import android.graphics.Bitmap
import com.kotliners.piedrapapeltijera.utils.media.ScreenshotSaver

/* Clase para la gestión de los eventos que desencadena ganar una partida,
y centralizar la lógica en una función.
 */
class VictoryManager {

    // Función suspend para gestionar la victoria sin bloquear la interfaz
    suspend fun handleVictory(
        context: Context,
        bitmap: Bitmap,
    ) {
        // Guardamos la captura de pantalla
        ScreenshotSaver.saveToMediaStore(context, bitmap)
    }
}