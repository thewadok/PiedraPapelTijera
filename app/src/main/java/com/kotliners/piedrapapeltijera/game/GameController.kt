package com.kotliners.piedrapapeltijera.game

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.kotliners.piedrapapeltijera.game.*

/**
 * Controlador principal del juego.
 *
 * 🧩 Forma parte de la capa "Controlador" del patrón MVC.
 * Se comunica con:
 *  - La vista (GameScreen)
 *  - El modelo (PlayerState + GameLogic)
 *
 * 🔹 Gestiona:
 *   - Validación de apuestas
 *   - Ejecución de la jugada
 *   - Actualización de monedas
 *   - Mensajes del resultado
 *
 * 🔸 Preparado para SQLite (ver secciones marcadas con "SQLite").
 */
class GameController(
    private val context: Context,
    private val playerState: PlayerState
) {
    // Estados observables por la vista (Compose)
    var lastMessage = mutableStateOf("")
        private set
    var lastResult = mutableStateOf<GameResult?>(null)
        private set
    var lastComputerMove = mutableStateOf<Move?>(null)
        private set
    var lastUserMove = mutableStateOf<Move?>(null)
        private set

    init {
        /**
         * Carga el estado inicial del jugador (monedas actuales).
         *
         * Actualmente se hace desde SharedPreferences.
         *
         * 🧩 En el futuro, cuando SQLite esté integrado:
         *     - Aquí se llamará a playerState.load(dao)
         *       (en lugar de playerState.load(context))
         */
        playerState.load(context)
    }

    /**
     * Ejecuta una ronda del juego.
     *
     * 1️⃣ Comprueba si la apuesta es válida.
     * 2️⃣ Usa GameLogic para decidir el resultado.
     * 3️⃣ Actualiza las monedas según el resultado.
     * 4️⃣ Guarda el nuevo estado del jugador.
     * 5️⃣ Genera un mensaje para mostrar en la vista.
     */
    fun playRound(betAmount: Int, move: Move) {
        if (!playerState.bet(betAmount)) {
            lastMessage.value = "Apuesta inválida."
            return
        }

        // 🔹 Lógica principal del juego (modelo puro)
        val (result, computerMove) = GameLogic.play(move)
        lastResult.value = result
        lastComputerMove.value = computerMove
        lastUserMove.value = move

        // 🔹 Actualiza monedas según el resultado
        playerState.updateCoins(result)

        /**
         * 🔹 Guarda el nuevo estado (saldo actualizado)
         *
         * Actualmente: usa SharedPreferences.
         *
         * 🧩 Futuro con SQLite:
         *     playerState.save(dao)
         */
        playerState.save(context)

        // 🔹 Mensaje informativo
        lastMessage.value = when (result) {
            GameResult.GANAS -> "¡Ganaste ${playerState.lastBet} monedas!"
            GameResult.PIERDES -> "Perdiste ${playerState.lastBet} monedas."
            GameResult.EMPATE -> "Empate, sin cambios."
        }
    }

}