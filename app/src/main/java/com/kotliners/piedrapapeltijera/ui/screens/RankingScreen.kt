package com.kotliners.piedrapapeltijera.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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

@Composable
fun RankingScreen(gameViewModel: GameViewModel) {
    val ranking by gameViewModel.rankingJugadores.collectAsState(initial = emptyList())

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text(
                text = "Ranking de Jugadores",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        itemsIndexed(ranking) { index, jugador ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("${index + 1}. ${jugador.nombre_usuario}", style = MaterialTheme.typography.titleLarge)
                    Text("Victorias: ${jugador.total_victorias}")
                    Text("Derrotas: ${jugador.total_derrotas}")
                    Text("Empates: ${jugador.total_empates}")
                }
            }
        }
    }
}