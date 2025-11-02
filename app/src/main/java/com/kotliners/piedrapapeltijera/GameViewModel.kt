package com.kotliners.piedrapapeltijera

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kotliners.piedrapapeltijera.data.HistorialPartida
import com.kotliners.piedrapapeltijera.data.Jugada
import com.kotliners.piedrapapeltijera.data.Jugador
import com.kotliners.piedrapapeltijera.data.Resultado
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = (application as GameApplication).database.gameDao()

    val statsJugador: Flow<Jugador?> = dao.getEstadisticasJugador(1)
    val historialJugador: Flow<List<HistorialPartida>> = dao.getHistorialDeJugador(1)
    val rankingJugadores: Flow<List<Jugador>> = dao.getRankingJugadores()

    fun registrarPartida(jugada: Jugada, cpu: Jugada, resultado: String) {
        viewModelScope.launch {
            dao.insertarPartida(
                HistorialPartida(
                    id_jugador = 1, // Asumimos que jugamos con el Jugador ID 1
                    jugada_jugador = jugada,
                    jugada_cpu = cpu,
                    resultado = Resultado.valueOf(resultado),
                    fecha_partida = System.currentTimeMillis().toString()
                )
            )

            when (resultado) {
                "VICTORIA" -> dao.sumarVictoria(1)
                "DERROTA" -> dao.sumarDerrota(1)
                "EMPATE" -> dao.sumarEmpate(1)
            }
        }
    }

    init {
        viewModelScope.launch {
            dao.insertarJugador(Jugador(id_jugador = 1, nombre_usuario = "Invitado"))
        }
    }
}