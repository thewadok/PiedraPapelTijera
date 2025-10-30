package com.kotliners.piedrapapeltijera.game

import android.content.Context

/**
 * Modelo que representa el estado del jugador:
 * - Monedas actuales
 * - Última apuesta realizada
 *
 *
 * 🔹 Actualmente guarda los datos con SharedPreferences.
 * 🔹 Preparado para cambiar fácilmente a SQLite (ver comentarios marcados con "SQLite").
 */
data class PlayerState(
    var coins: Int = 500,
    var lastBet: Int = 0
) {
    companion object {
        private const val PREFS_NAME = "player_prefs"
        private const val KEY_COINS = "coins"
    }

    /**
     * 🔸 Carga el número de monedas guardado localmente.
     *
     * 👉 Actualmente usa SharedPreferences.
     * 👉 Cuando se integre SQLite, este método deberá leer desde la tabla de jugadores (por ejemplo, `player_state`).
     */
    fun load(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        coins = prefs.getInt(KEY_COINS, 500)

        // 🧩 VERSIÓN FUTURA CON SQLITE:
        // val playerEntity = dao.getPlayer()
        // coins = playerEntity?.coins ?: 500
    }

    /**
     * 🔸 Guarda el número actual de monedas.
     *
     * 👉 Actualmente usa SharedPreferences.
     * 👉 Cuando esté lista la bbdd, este método actualizará la base de datos SQLite con el nuevo saldo del jugador.
     */
    fun save(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_COINS, coins).apply()

        // 🧩 VERSIÓN FUTURA CON SQLITE:
        // dao.updatePlayerCoins(coins)
    }

    /**
     * 🔸 Intenta realizar una apuesta.
     * Devuelve `true` si el jugador tiene suficientes monedas.
     */
    fun bet(amount: Int): Boolean {
        return if (amount > 0 && amount <= coins) {
            lastBet = amount
            true
        } else {
            false
        }
    }

    /**
     * 🔸 Actualiza las monedas según el resultado del juego.
     * Si el jugador pierde, nunca puede quedar con saldo negativo.
     */
    fun updateCoins(result: GameResult) {
        when (result) {
            GameResult.GANAS -> coins += lastBet
            GameResult.PIERDES -> coins = (coins - lastBet).coerceAtLeast(0)
            GameResult.EMPATE -> { /* No cambia el saldo */ }
        }
    }
}