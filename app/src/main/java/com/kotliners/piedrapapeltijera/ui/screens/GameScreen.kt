package com.kotliners.piedrapapeltijera.ui.screens

import android.Manifest
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.kotliners.piedrapapeltijera.utils.location.LocationManager
import com.kotliners.piedrapapeltijera.utils.media.SoundEffects
import com.kotliners.piedrapapeltijera.utils.media.rememberCaptureCurrentView
import kotlinx.coroutines.launch

@Composable
fun GameScreen(viewModel: MainViewModel = viewModel()) {

    var userMove by remember { mutableStateOf<Move?>(null) }
    var computerMove by remember { mutableStateOf<Move?>(null) }
    var result by remember { mutableStateOf<GameResult?>(null) }
    var betAmount by remember { mutableIntStateOf(10) }
    var message by remember { mutableStateOf("") }

    val context = LocalContext.current
    val saldo = viewModel.monedas.observeAsState(0).value
    val partidas = viewModel.partidas.observeAsState(0).value

    val locationManager = remember { LocationManager(context) }
    val coroutineScope = rememberCoroutineScope()
    val captureView = rememberCaptureCurrentView()

    var pendingMove by remember { mutableStateOf<Move?>(null) }

    // --------------------------
    // FUNCIÓN PRINCIPAL DE JUEGO
    // --------------------------
    fun jugarCon(mov: Move, location: Location?) {
        if (betAmount !in 1..saldo) {
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
                viewModel.registrarPartida(
                    mov, c, r, betAmount,
                    location?.latitude,
                    location?.longitude
                )
                message = context.getString(R.string.you_won, betAmount)

                SoundEffects.playWin()
                val screenshot = captureView()
                viewModel.onPlayerWin(context, screenshot)

                VictoryManager.handleResult(context, r, durationMs)
            }

            GameResult.PIERDES -> {
                viewModel.cambiarMonedas(-betAmount)
                viewModel.registrarPartida(
                    mov, c, r, betAmount,
                    location?.latitude,
                    location?.longitude
                )
                message = context.getString(R.string.you_lost, betAmount)

                SoundEffects.playLose()
            }

            GameResult.EMPATE -> {
                viewModel.registrarPartida(
                    mov, c, r, betAmount,
                    location?.latitude,
                    location?.longitude
                )
                message = context.getString(R.string.tie)
            }
        }
    }

    // ---------------------------------------
    // SOLICITUD DE PERMISO DE UBICACIÓN
    // ---------------------------------------
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        coroutineScope.launch {
            val loc = if (granted) locationManager.getCurrentLocation() else null
            pendingMove?.let { jugarCon(it, loc) }
        }
    }

    fun iniciarJuego(mov: Move) {
        pendingMove = mov
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // ----------------------
    // UI DE LA PANTALLA
    // ----------------------
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
                .padding(16.dp)
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

            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.bet_your_coins),
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextoBlanco
                )

                // Selector Apuesta
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            SoundEffects.playClick()
                            if (betAmount > 10) betAmount -= 10
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("-", fontSize = 125.sp, color = TextoBlanco)
                    }

                    Text(
                        "$betAmount ${stringResource(R.string.coins)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = AmarilloNeon
                    )

                    Button(
                        onClick = {
                            SoundEffects.playClick()
                            if (betAmount + 10 <= saldo) betAmount += 10
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("+", fontSize = 75.sp, color = TextoBlanco)
                    }
                }

                Text(
                    stringResource(R.string.choose_move),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoBlanco
                )

                // BOTONES: PIEDRA / PAPEL / TIJERA
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            SoundEffects.playClick()
                            iniciarJuego(Move.PIEDRA)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.icono_piedra_neon),
                            contentDescription = stringResource(R.string.rock),
                            modifier = Modifier.size(95.dp)
                        )
                    }

                    Button(
                        onClick = {
                            SoundEffects.playClick()
                            iniciarJuego(Move.PAPEL)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.icono_papel_neon),
                            contentDescription = stringResource(R.string.paper),
                            modifier = Modifier.size(95.dp)
                        )
                    }

                    Button(
                        onClick = {
                            SoundEffects.playClick()
                            iniciarJuego(Move.TIJERA)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.icono_tijera_neon),
                            contentDescription = stringResource(R.string.scissors),
                            modifier = Modifier.size(110.dp)
                        )
                    }
                }

                // RESULTADO
                if (result != null) {
                    Spacer(Modifier.height(16.dp))

                    Text(
                        stringResource(
                            R.string.player_vs_bank,
                            userMove?.name ?: "—",
                            computerMove?.name ?: "—"
                        ),
                        color = TextoBlanco
                    )

                    Text(message, color = TextoBlanco)
                    Text(
                        stringResource(R.string.current_balance, saldo),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextoBlanco
                    )
                }
            }
        }
    }
}