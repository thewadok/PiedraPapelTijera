package com.kotliners.piedrapapeltijera.game

// Lógica del juego
object GameLogic {

    fun play(userMove: Move): Pair<GameResult, Move> {
        // Elección aleatoria de la máquina
        //val computerMove = Move.entries.toTypedArray().random()
        val computerMove = Move.values().random()


        val result = when {
            userMove == computerMove -> GameResult.EMPATE
            userMove == Move.PIEDRA && computerMove == Move.TIJERA -> GameResult.GANAS
            userMove == Move.PAPEL && computerMove == Move.PIEDRA -> GameResult.GANAS
            userMove == Move.TIJERA && computerMove == Move.PAPEL -> GameResult.GANAS
            else -> GameResult.PIERDES
        }

        return Pair(result, computerMove)
    }
}