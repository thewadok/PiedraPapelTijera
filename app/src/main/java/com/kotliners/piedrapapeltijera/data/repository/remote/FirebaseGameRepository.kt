package com.kotliners.piedrapapeltijera.data.repository.remote

import com.kotliners.piedrapapeltijera.data.remote.firebase.FirebaseApiService
import com.kotliners.piedrapapeltijera.data.remote.firebase.JugadorRemoto
import com.kotliners.piedrapapeltijera.data.remote.firebase.PartidaRemota
import com.kotliners.piedrapapeltijera.data.remote.firebase.PremioComunRemoto
import com.kotliners.piedrapapeltijera.data.remote.firebase.RetrofitInstance

/**
 * Repositorio remoto que centraliza el acceso a Firebase.
 * Aquí no hay lógica de UI, solo acceso a datos.
 * Actúa como capa intermedia entre: Retrofit/Moshi y el resto de la app como seria:
 * ViewModels, pantallas, lógica de juego
 */
class FirebaseGameRepository(
    // API real creada por RetrofitInstance
    private val api: FirebaseApiService = RetrofitInstance.api
) {

    /**
     * Devuelve todos los jugadores exactamente como están en Firebase.
     * Clave: UID del jugador
     * Valor: objeto JugadorRemoto
     */
    suspend fun fetchJugadores(): Map<String, JugadorRemoto> {
        return api.getJugadores()
    }

    /**
     * Devuelve los 10 mejores jugadores ordenados por número de victorias.
     * Se ordena en el repositorio para no forzar al profesor a usar Firebase queries.
     */
    suspend fun fetchTopJugadores(limit: Int = 10): List<JugadorRemoto> {
        val mapa = api.getJugadores()

        // Convertimos el mapa en lista y ordenamos por victorias desc.
        return mapa.values
            .sortedWith(
                compareByDescending<JugadorRemoto> { it.monedas ?: 0 }
                    .thenByDescending { (it.victorias ?: 0) - (it.derrotas ?: 0) }
            )
            .take(limit)
    }

    /**
     * Devuelve todas las partidas remotas.
     * No lo usas aún, pero está preparado para añadir ranking de partidas o historial remoto.
     */
    suspend fun fetchPartidas(): Map<String, PartidaRemota> {
        return api.getPartidas()
    }

    /**
     * Devuelve el estado actual del premio común compartido por todos los jugadores.
     */
    suspend fun fetchPremioComun(): PremioComunRemoto {
        return api.getPremioComun()
    }
}
