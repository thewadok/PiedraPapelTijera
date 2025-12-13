package com.kotliners.piedrapapeltijera.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotliners.piedrapapeltijera.ui.viewmodel.RankingViewModel
import com.kotliners.piedrapapeltijera.ui.components.TituloPrincipal
import androidx.compose.ui.text.font.FontWeight
import com.kotliners.piedrapapeltijera.ui.theme.*


@Composable
fun RankingScreen(viewModel: RankingViewModel = viewModel()) {

    val jugadores by viewModel.topJugadores.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        TituloPrincipal(text = "Top 10 Jugadores")

        Spacer(modifier = Modifier.height(16.dp))

        when {
            loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            else -> {
                LazyColumn {
                    itemsIndexed(jugadores) { index, jugador ->

                        val monedas = jugador.monedas ?: 0
                        val victorias = jugador.victorias ?: 0
                        val derrotas = jugador.derrotas ?: 0
                        val balance = victorias - derrotas
                        val balanceTxt = if (balance >= 0) "+$balance" else "$balance"

                        val colorBalance = if (balance >= 0) AmarilloNeon else RosaNeon

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = AzulNeon,
                                contentColor = TextoNegro
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${index + 1}. ${jugador.nombre ?: "Jugador"}",
                                    color = TextoNegro,
                                    fontWeight = FontWeight.Bold
                                )
                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "$monedas Monedas",
                                        color = TextoNegro,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "$balanceTxt Balance victorias",
                                        color = colorBalance,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}