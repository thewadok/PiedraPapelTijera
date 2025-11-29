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
import com.kotliners.piedrapapeltijera.utils.location.LocationManager
import com.kotliners.piedrapapeltijera.utils.media.SoundEffects
import androidx.compose.runtime.getValue
import com.kotliners.piedrapapeltijera.notifications.VictoryNotification
import com.kotliners.piedrapapeltijera.ui.components.VictoryDialog
import com.kotliners.piedrapapeltijera.utils.calendar.rememberCalendarPermissionState
import com.kotliners.piedrapapeltijera.utils.calendar.CalendarHelper
import com.kotliners.piedrapapeltijera.utils.locale.moveLabel
import com.kotliners.piedrapapeltijera.utils.media.rememberCaptureCurrentView
import kotlinx.coroutines.launch
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.compose.animation.Crossfade
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import com.kotliners.piedrapapeltijera.ui.theme.AzulNeon
import com.kotliners.piedrapapeltijera.ui.theme.RosaNeon

@Composable
fun GameScreen(viewModel: MainViewModel = viewModel()) {

    // Estado del juego
    var userMove by remember { mutableStateOf<Move?>(null) }
    var computerMove by remember { mutableStateOf<Move?>(null) }
    var result by remember { mutableStateOf<GameResult?>(null) }
    var betAmount by remember { mutableIntStateOf(10) }
    var message by remember { mutableStateOf("") }
    var showVictoryDialog by remember { mutableStateOf(false) }

    // Estado para bloquear nuevos turnos
    var isRoundInProgress by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val saldo = viewModel.monedas.observeAsState(0).value
    val partidas = viewModel.partidas.observeAsState(0).value

    // Utilidades media
    // Función para capturar la pantalla
    val captureView = rememberCaptureCurrentView()

    // Permisos de calendario
    val (requestCalendarPermissions, calendarGranted) = rememberCalendarPermissionState()

    // Scope para coroutinas
    val coroutineScope = rememberCoroutineScope()

    // Gestión de ubicación
    val locationManager = remember { LocationManager(context) }

    var pendingMove by remember { mutableStateOf<Move?>(null) }

    // Función principal del juego
    fun jugarCon(mov: Move, location: Location?) {
        if (betAmount !in 1..saldo) {
            message = context.getString(R.string.invalid_bet)

            // Desbloqueamos porque no se ha jugado realmente
            isRoundInProgress = false
            pendingMove = null
            return
        }

        val startTime = System.currentTimeMillis()
        val (r, c) = GameLogic.play(mov)
        val durationMs = (System.currentTimeMillis() - startTime).coerceAtLeast(100)

        // Lanzamos corrutina para aplicar el resultado y desbloquear después de 3s
        coroutineScope.launch {
            kotlinx.coroutines.delay(2000)

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

                    // Mostramos notificación de victoria con tiempo real
                    VictoryNotification.show(context, durationMs)

                    // Mostramos dialogo con opcion de guardar la captura de pantalla
                    showVictoryDialog = true

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
            isRoundInProgress = false
            pendingMove = null
        }
    }

    // Solicitud de permisos de ubicación.
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()

    ) { isGranted: Boolean ->
        coroutineScope.launch {
            val loc: Location? = if (isGranted) {
                // Si da permiso, intentamos obtener la ubicación
                locationManager.getCurrentLocation()
            } else {

                // Si no da permiso, seguimos sin ubicación
                null
            }
            // Siempre jugamos, con o sin ubicación
            pendingMove?.let { jugarCon(it, loc) }
        }
    }

    fun iniciarJuego(mov: Move) {

        // Si ya hay una jugada en progreso, no permitimos más
        if (isRoundInProgress) return

        // Marcamos que empieza una jugada
        isRoundInProgress = true
        pendingMove = mov

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            // Si tenemos permiso, jugamos y sacamos la ubicación en segundo plano
            coroutineScope.launch {
                val location = locationManager.getCurrentLocation()
                jugarCon(mov, location)
            }
        } else {

            // Solo si nO hay permiso, mostramos el diálogo del sistema
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // UI de la pantalla
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
                modifier = Modifier.padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.bet_your_coins),
                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 26.sp,
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
                        fontSize = 26.sp,
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

                // Texto con animación fade: elegir jugada / esperando resultado
                Crossfade(targetState = isRoundInProgress, label = "move_text_fade") { blocked ->
                    if (blocked) {
                        Text(
                            text = stringResource(R.string.wait_for_result),
                            style = MaterialTheme.typography.headlineSmall,
                            fontSize = 26.sp,
                            color = Color.Gray
                        )
                    } else {
                        Text(
                            stringResource(R.string.choose_move),
                            style = MaterialTheme.typography.headlineSmall,
                            fontSize = 26.sp,
                            color = TextoBlanco
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                val buttonsAlpha = if (isRoundInProgress) 0.4f else 1f

                // Botones: Piedra, Papel, Tijera

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .alpha(buttonsAlpha),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // PIEDRA
                    Button(
                        onClick = {
                            SoundEffects.playClick()
                            iniciarJuego(Move.PIEDRA)
                        },
                        enabled = !isRoundInProgress,
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

                    // PAPEL
                    Button(
                        onClick = {
                            SoundEffects.playClick()
                            iniciarJuego(Move.PAPEL)
                        },
                        enabled = !isRoundInProgress,
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

                    // TIJERA
                    Button(
                        onClick = {
                            SoundEffects.playClick()
                            iniciarJuego(Move.TIJERA)
                        },
                        enabled = !isRoundInProgress,
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

                // Resultado
                if (result != null) {
                    Spacer(Modifier.height(10.dp))

                    val userMoveText = moveLabel(userMove)
                    val computerMoveText = moveLabel(computerMove)

                    Text(
                        text = stringResource(
                            R.string.player_vs_bank,
                            userMoveText,
                            computerMoveText
                        ),
                        color = AzulNeon,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        message,
                        color = AmarilloNeon,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                        )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.current_balance, saldo),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.headlineSmall,
                        color = RosaNeon
                    )
                }

                // Mostramos el dialogo de victoria
                if (showVictoryDialog) {
                    VictoryDialog(
                        onConfirm = { saveScreenshot, addCalendar ->
                            showVictoryDialog = false


                            val eventTitle = context.getString(R.string.calendar_event_title)
                            val eventDescription = context.getString(
                                R.string.calendar_event_description,
                                betAmount,
                                saldo
                            )

                            coroutineScope.launch {

                                // Guardar captura
                                if (saveScreenshot) {
                                    // Capturamos la pantalla solo si el usuario quiere guardarla
                                    val screenshot = captureView()

                                    // Llamamos a la corrutina onPlayerWin cuando hay victoria
                                    viewModel.onPlayerWin(
                                        context = context,
                                        screenshot = screenshot
                                    )
                                }

                                // Añadir al calendario
                                if (addCalendar) {
                                    if (!calendarGranted.value) {
                                        requestCalendarPermissions()
                                    } else {
                                        CalendarHelper.insertVictoryEvent(
                                            context = context,
                                            title = eventTitle,
                                            description = eventDescription
                                        )
                                    }
                                }
                            }
                        },

                        onDismiss = {
                            // Cerramos el diálogo sin hacer nada
                            showVictoryDialog = false
                        }
                    )
                }
            }
        }
    }
}