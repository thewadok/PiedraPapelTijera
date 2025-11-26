package com.kotliners.piedrapapeltijera.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotliners.piedrapapeltijera.data.local.entity.Partida
import com.kotliners.piedrapapeltijera.ui.viewmodel.MainViewModel
import com.kotliners.piedrapapeltijera.data.repository.PartidaRepository
import com.kotliners.piedrapapeltijera.game.GameResult
import com.kotliners.piedrapapeltijera.ui.components.TituloPrincipal
import com.kotliners.piedrapapeltijera.ui.theme.AmarilloNeon
import com.kotliners.piedrapapeltijera.ui.theme.FondoNegro
import com.kotliners.piedrapapeltijera.ui.theme.TextoBlanco
import com.kotliners.piedrapapeltijera.ui.theme.AmarilloNeon
import com.kotliners.piedrapapeltijera.ui.components.TituloPrincipal
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(viewModel: MainViewModel = viewModel()) {
    val partidas = viewModel.historialPartidas.observeAsState(emptyList()).value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoNegro)
            .padding(16.dp)
    ) {

        TituloPrincipal("Historial de partidas")

        Spacer(Modifier.height(8.dp))

        if (partidas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Todavía no has jugado ",
                    color = TextoBlanco,
                    fontSize = 18.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(partidas) { partida ->
                    PartidaCard(partida)
                }
            }
        }
    }
}

@Composable
private fun PartidaCard(p: Partida) {
    val fechaFormateada = SimpleDateFormat(
        "dd/MM/yyyy HH:mm",
        Locale.getDefault()
    ).format(Date(p.fecha))

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.08f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = fechaFormateada,
                color = AmarilloNeon,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Tú: ${p.jugadaJugador}  |  Banca: ${p.jugadaCpu}",
                color = TextoBlanco,
                fontSize = 16.sp
            )

            Text(
                text = "Resultado: ${resultadoTexto(p.resultado)}",
                color = TextoBlanco,
                fontSize = 16.sp
            )

            Text(
                text = "Apuesta: ${p.apuesta} monedas",
                color = TextoBlanco,
                fontSize = 15.sp
            )

            val cambio = if (p.cambioMonedas >= 0) "+${p.cambioMonedas}" else "${p.cambioMonedas}"
            Text(
                text = "Cambio saldo: $cambio",
                color = TextoBlanco,
                fontSize = 15.sp
            )

            if (p.latitud != null && p.longitud != null) {
                Text(
                    text = "Localización: Lat ${p.latitud}, Lon ${p.longitud}",
                    color = TextoBlanco,
                    fontSize = 14.sp
                )
            } else {
                Text(
                    text = "No se han obtenido parámetros de longitud ni latutud",
                    color = TextoBlanco,
                    fontSize = 14.sp
                )
            }
        }
    }
}

    private fun resultadoTexto(r: GameResult): String = when (r) {
        GameResult.GANAS -> "GANASTE "
        GameResult.PIERDES -> "PERDISTE "
        GameResult.EMPATE -> "EMPATE "
    }
