package com.kotliners.piedrapapeltijera.utils

import android.content.Context
import com.kotliners.piedrapapeltijera.notifications.VictoryNotification
import com.kotliners.piedrapapeltijera.game.GameResult

/**
 * Gestiona los resultados de la partida (victoria, derrota, empate)
 * y lanza efectos o notificaciones según el caso.
 */
object VictoryManager {

    /**
     * Maneja el resultado y lanza la notificación si corresponde.
     *
     * @param context Contexto actual.
     * @param result Resultado del juego (GANAS, PIERDES, EMPATE).
     * @param durationMs Tiempo en milisegundos que tardó la partida.
     */
    fun handleResult(context: Context, result: GameResult, durationMs: Long) {
        when (result) {
            GameResult.GANAS -> onVictory(context, durationMs)
            GameResult.PIERDES -> onDefeat(context)
            GameResult.EMPATE -> onDraw(context)
        }
    }

    private fun onVictory(context: Context, durationMs: Long) {
        // 🔔 Mostrar notificación de victoria con tiempo real
        VictoryNotification.show(context, durationMs)
    }

    private fun onDefeat(context: Context) {
        // (Futuro) sonido o efecto de derrota
    }

    private fun onDraw(context: Context) {
        // (Futuro) animación o vibración de empate
    }
}