package com.kotliners.piedrapapeltijera.data.remote.firebase

import retrofit2.http.GET

// Interfaz de Retrofit y Moshi
/** Esta es la interfaz de Retrofit donde se definen los endpoints que vamos a usar de Firebase
 * Esta interfaz se usa para saber a que URL debe llamar y que tipo de datos debe devolcer
 * La usara RetrofitInstance para crear la API real
 * La usará FirebaseGameRepository para acceder a esos datos de una forma más sencilla
 */
interface FirebaseApiService {

    // GET a .../jugadores.json
    // Devuelve un mapa: UID -> JugadorRemoto
    @GET("jugadores.json")
    suspend fun getJugadores(): Map<String, JugadorRemoto>

    // GET a .../partidas.json
    // Devuelve un mapa: idPartida -> PartidaRemota
    @GET("partidas.json")
    suspend fun getPartidas(): Map<String, PartidaRemota>

    // GET a .../premioComun.json
    // Devuelve el objeto del premio común
    @GET("premioComun.json")
    suspend fun getPremioComun(): PremioComunRemoto
}