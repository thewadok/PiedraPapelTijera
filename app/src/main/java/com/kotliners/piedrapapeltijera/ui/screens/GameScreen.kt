package com.kotliners.piedrapapeltijera.ui.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kotliners.piedrapapeltijera.R
import com.kotliners.piedrapapeltijera.game.GameController
import com.kotliners.piedrapapeltijera.game.Move
import com.kotliners.piedrapapeltijera.game.PlayerState
import com.kotliners.piedrapapeltijera.ui.theme.FondoNegro
import com.kotliners.piedrapapeltijera.ui.theme.AmarilloNeon
import com.kotliners.piedrapapeltijera.ui.theme.TextoBlanco


@Composable
fun GameScreen() {
    val context = LocalContext.current
    val playerState = remember { PlayerState() }
    val controller = remember { GameController(context, playerState) }

    var betAmount by remember { mutableStateOf(10) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoNegro)
    ) {
        // Monedas totales (arriba a la derecha)
        Text(
            text = "${playerState.coins} monedas",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            style = MaterialTheme.typography.titleLarge,
            color = AmarilloNeon
        )

        // Contenido principal
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "¡Apuesta tus monedas!",
                style = MaterialTheme.typography.headlineSmall,
                color = TextoBlanco
            )

            // Selector de apuesta (+ / -)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { if (betAmount > 10) betAmount -= 10 },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text("➖", style = MaterialTheme.typography.headlineLarge, color = TextoBlanco)
                }

                Text(
                    "$betAmount monedas",
                    style = MaterialTheme.typography.titleLarge,
                    color = AmarilloNeon
                )

                Button(
                    onClick = { if (betAmount + 10 <= playerState.coins) betAmount += 10 },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text("➕", style = MaterialTheme.typography.headlineLarge, color = TextoBlanco)
                }
            }

            Text(
                "Elige tu jugada:",
                style = MaterialTheme.typography.titleMedium,
                color = TextoBlanco
            )

            // Botones de jugada (vista pura)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { controller.playRound(betAmount, Move.PIEDRA) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icono_piedra_neon),
                        contentDescription = "Piedra",
                        modifier = Modifier.size(95.dp)
                    )
                }

                Button(
                    onClick = { controller.playRound(betAmount, Move.PAPEL) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icono_papel_neon),
                        contentDescription = "Papel",
                        modifier = Modifier.size(95.dp)
                    )
                }

                Button(
                    onClick = { controller.playRound(betAmount, Move.TIJERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icono_tijera_neon),
                        contentDescription = "Tijera",
                        modifier = Modifier.size(110.dp)
                    )
                }
            }

            // Resultado y saldo actual
            if (controller.lastResult.value != null) {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Jugador: ${controller.lastUserMove.value?.name} | Banca: ${controller.lastComputerMove.value?.name}",
                    color = TextoBlanco
                )
                Text(
                    controller.lastMessage.value,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoBlanco
                )
                Text(
                    "Saldo actual: ${playerState.coins}",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoBlanco
                )
            }
        }
    }
}