package com.kotliners.piedrapapeltijera.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotliners.piedrapapeltijera.GameViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(gameViewModel: GameViewModel) {
    val historial by gameViewModel.historialJugador.collectAsState(initial = emptyList())

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text(
                text = "Historial de Partidas",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(historial) { partida ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    val date = Date(partida.fecha_partida.toLong())

                    Text("Fecha: ${sdf.format(date)}")
                    Text("Tu jugada: ${partida.jugada_jugador}")
                    Text("Jugada CPU: ${partida.jugada_cpu}")
                    Text("Resultado: ${partida.resultado}")
                }
            }
        }
    }
}