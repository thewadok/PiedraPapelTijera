package com.kotliners.piedrapapeltijera.game

import android.content.Context
import com.kotliners.piedrapapeltijera.utils.VictoryManager

object GameLogic {

    fun play(context: Context, userMove: Move): Pair<GameResult, Move> {
        val computerMove = Move.values().random()

        val result = when {
            userMove == computerMove -> GameResult.EMPATE
            userMove == Move.PIEDRA && computerMove == Move.TIJERA -> GameResult.GANAS
            userMove == Move.PAPEL && computerMove == Move.PIEDRA -> GameResult.GANAS
            userMove == Move.TIJERA && computerMove == Move.PAPEL -> GameResult.GANAS
            else -> GameResult.PIERDES
        }

        // 🔗 Delegamos el evento al VictoryManager
        VictoryManager.handleResult(context, result)

        return Pair(result, computerMove)
    }
}