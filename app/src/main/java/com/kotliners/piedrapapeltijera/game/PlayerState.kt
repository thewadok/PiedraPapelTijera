package com.kotliners.piedrapapeltijera.game

data class PlayerState(
    var coins: Int = 500,
    var lastBet: Int = 0
) {
    fun bet(amount: Int): Boolean {
        return if (amount > 0 && amount <= coins) {
            lastBet = amount
            true
        } else {
            false
        }
    }

    fun updateCoins(result: GameResult) {
        when (result) {
            GameResult.GANAS -> coins += lastBet
            GameResult.PIERDES -> coins -= lastBet
            GameResult.EMPATE -> {} // no cambia
        }
    }
}