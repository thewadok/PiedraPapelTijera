package com.kotliners.piedrapapeltijera.utils

import android.content.Context
import com.kotliners.piedrapapeltijera.notifications.VictoryNotification
import com.kotliners.piedrapapeltijera.game.GameResult

/**
 * Clase encargada de manejar los eventos de resultado del juego.
 *
 */
object VictoryManager {

    /**
     * Maneja el resultado de la partida.
     *
     * @param context Contexto actual de la app (necesario para notificaciones).
     * @param result Resultado del juego (GANAS, PIERDES o EMPATE).
     */
    fun handleResult(context: Context, result: GameResult) {
        when (result) {
            GameResult.GANAS -> onVictory(context)
            GameResult.PIERDES -> onDefeat(context)
            GameResult.EMPATE -> onDraw(context)
        }
    }

    private fun onVictory(context: Context) {
        // 🔔 Muestra la notificación de victoria
        VictoryNotification.show(context)
    }

    private fun onDefeat(context: Context) {
        // (Futuro) Efecto o sonido de derrota
    }

    private fun onDraw(context: Context) {
        // (Futuro) Efecto visual de empate
    }
}