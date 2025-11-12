package com.kotliners.piedrapapeltijera.ui.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotliners.piedrapapeltijera.R
import com.kotliners.piedrapapeltijera.game.GameLogic
import com.kotliners.piedrapapeltijera.game.GameResult
import com.kotliners.piedrapapeltijera.game.Move
import com.kotliners.piedrapapeltijera.ui.theme.FondoNegro
import com.kotliners.piedrapapeltijera.ui.theme.AmarilloNeon
import com.kotliners.piedrapapeltijera.ui.theme.TextoBlanco
import com.kotliners.piedrapapeltijera.ui.viewmodel.MainViewModel
import com.kotliners.piedrapapeltijera.ui.components.NeonGloboInfo
import com.kotliners.piedrapapeltijera.utils.VictoryManager

@Composable
fun GameScreen(viewModel: MainViewModel = viewModel()) {

    var userMove by remember { mutableStateOf<Move?>(null) }
    var computerMove by remember { mutableStateOf<Move?>(null) }
    var result by remember { mutableStateOf<GameResult?>(null) }
    var betAmount by remember { mutableStateOf(10) }
    var message by remember { mutableStateOf("") }

    val context = LocalContext.current

    val saldo = viewModel.monedas.observeAsState(0).value
    val partidas = viewModel.partidas.observeAsState(0).value

    fun jugarCon(mov: Move) {
        if (betAmount <= 0 || betAmount > saldo) {
            message = context.getString(R.string.invalid_bet)
            return
        }

        val startTime = System.currentTimeMillis()
        val (r, c) = GameLogic.play(mov)
        val durationMs = (System.currentTimeMillis() - startTime).coerceAtLeast(100)

        result = r
        computerMove = c
        userMove = mov

        when (r) {
            GameResult.GANAS -> {
                viewModel.cambiarMonedas(+betAmount)
                viewModel.registrarPartida(mov, c, r, betAmount)
                message = context.getString(R.string.you_won, betAmount)
                VictoryManager.handleResult(context, r, durationMs)
            }
            GameResult.PIERDES -> {
                viewModel.cambiarMonedas(-betAmount)
                viewModel.registrarPartida(mov, c, r, betAmount)
                message = context.getString(R.string.you_lost, betAmount)
            }
            GameResult.EMPATE -> {
                viewModel.registrarPartida(mov, c, r, betAmount)
                message = context.getString(R.string.tie)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoNegro)
    ) {
        val scroll = rememberScrollState()

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            NeonGloboInfo(
                partidas = partidas,
                saldo = saldo,
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .heightIn(min = 84.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(24.dp))

            // Contenido principal
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Título
                Text(
                    text = stringResource(R.string.bet_your_coins),
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextoBlanco
                )

                // Selector de apuesta
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
                        Text("-", fontSize = 125.sp, color = TextoBlanco)
                    }

                    Text(
                        "$betAmount ${context.getString(R.string.coins)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = AmarilloNeon
                    )

                    Button(
                        onClick = { if (betAmount + 10 <= saldo) betAmount += 10 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text("+", fontSize = 75.sp, color = TextoBlanco)
                    }
                }

                Text(
                    text = stringResource(R.string.choose_move),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoBlanco
                )

                // Botones Piedra / Papel / Tijera
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Piedra
                    Button(
                        onClick = { jugarCon(Move.PIEDRA) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icono_piedra_neon),
                            contentDescription = stringResource(R.string.rock),
                            modifier = Modifier.size(95.dp)
                        )
                    }

                    // Papel
                    Button(
                        onClick = { jugarCon(Move.PAPEL) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icono_papel_neon),
                            contentDescription = stringResource(R.string.paper),
                            modifier = Modifier.size(95.dp)
                        )
                    }

                    // Tijera
                    Button(
                        onClick = { jugarCon(Move.TIJERA) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icono_tijera_neon),
                            contentDescription = stringResource(R.string.scissors),
                            modifier = Modifier
                                .size(110.dp)
                                .padding(end = 4.dp)
                        )
                    }
                }

                if (result != null) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(
                            R.string.player_vs_bank,
                            userMove?.name ?: "—",
                            computerMove?.name ?: "—"
                        ),
                        color = TextoBlanco
                    )
                    Text(
                        text = message,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextoBlanco
                    )
                    Text(
                        text = stringResource(R.string.current_balance, saldo),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextoBlanco
                    )
                }
            }
        }
    }
}