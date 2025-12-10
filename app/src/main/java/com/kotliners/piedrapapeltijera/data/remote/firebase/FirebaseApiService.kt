package com.kotliners.piedrapapeltijera.data.remote.firebase

import retrofit2.http.GET

// Interfaz de Retrofit y Moshi
interface FirebaseApiService {

    @GET("jugadores.json")
    suspend fun getJugadores(): Map<String, JugadorRemoto>

    @GET("partidas.json")
    suspend fun getPartidas(): Map<String, PartidaRemota>

    @GET("premioComun.json")
    suspend fun getPremioComun(): PremioComunRemoto
}