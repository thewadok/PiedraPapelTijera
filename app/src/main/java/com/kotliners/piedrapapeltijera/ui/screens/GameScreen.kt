package com.kotliners.piedrapapeltijera.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotliners.piedrapapeltijera.R
import com.kotliners.piedrapapeltijera.game.GameLogic
import com.kotliners.piedrapapeltijera.game.GameResult
import com.kotliners.piedrapapeltijera.game.Move
import com.kotliners.piedrapapeltijera.game.PlayerState
import com.kotliners.piedrapapeltijera.ui.theme.TextoBlanco

@Composable
fun GameScreen() {
    var userMove by remember { mutableStateOf<Move?>(null) }
    var computerMove by remember { mutableStateOf<Move?>(null) }
    var result by remember { mutableStateOf<GameResult?>(null) }
    var betAmount by remember { mutableIntStateOf(10) }
    var message by remember { mutableStateOf("") }
    var playerState by remember { mutableStateOf(PlayerState()) }

    Box(modifier = Modifier.fillMaxSize()) {

        //  Monedas totales
        Text(
            text = "💰 ${playerState.coins} monedas",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            style = MaterialTheme.typography.titleLarge,
            color = TextoBlanco
        )

        //  Contenido principal
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("¡Apuesta tus monedas!", style = MaterialTheme.typography.headlineSmall)

            // Selector de apuesta
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = { if (betAmount > 10) betAmount -= 10 }) {
                    Text("➖", fontSize = 24.sp)
                }

                Text(
                    text = "$betAmount monedas",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoBlanco
                )

                Button(onClick = { if (betAmount + 10 <= playerState.coins) betAmount += 10 }) {
                    Text("➕", fontSize = 24.sp)
                }
            }

            Text("Elige tu jugada:", style = MaterialTheme.typography.titleMedium)

            // ️ Botones de jugada
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GameButton("Piedra", R.drawable.icono_piedra_color, Move.PIEDRA, betAmount, playerState) {
                    result = it.first
                    computerMove = it.second
                    message = resultMessage(it.first, playerState.lastBet)
                }

                GameButton("Papel", R.drawable.icono_papel_color, Move.PAPEL, betAmount, playerState) {
                    result = it.first
                    computerMove = it.second
                    message = resultMessage(it.first, playerState.lastBet)
                }

                GameButton("Tijera", R.drawable.icono_tijera_color, Move.TIJERA, betAmount, playerState) {
                    result = it.first
                    computerMove = it.second
                    message = resultMessage(it.first, playerState.lastBet)
                }
            }

            // Resultado
            if (result != null) {
                Spacer(Modifier.height(16.dp))
                Text("Tú: ${userMove?.name} | Máquina: ${computerMove?.name}")
                Text(message, style = MaterialTheme.typography.titleMedium)
                Text("Saldo actual: ${playerState.coins} 🪙", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
private fun GameButton(
    label: String,
    icon: Int,
    move: Move,
    betAmount: Int,
    playerState: PlayerState,
    onResult: (Pair<GameResult, Move>) -> Unit
) {
    Button(
        onClick = {
            if (playerState.bet(betAmount)) {
                val (r, c) = GameLogic.play(move)
                playerState.updateCoins(r)
                onResult(Pair(r, c))
            }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.size(90.dp)
        )
    }
}

private fun resultMessage(result: GameResult, bet: Int): String =
    when (result) {
        GameResult.GANAS -> " ¡Ganaste $bet monedas!"
        GameResult.PIERDES -> " Perdiste $bet monedas."
        GameResult.EMPATE -> " Empate, sin cambios."
    }
