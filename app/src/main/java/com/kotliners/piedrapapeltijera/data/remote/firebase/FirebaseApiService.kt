package com.kotliners.piedrapapeltijera.data.remote.firebase
// Interfaz de Retrofit y Moshi

import retrofit2.http.GET

interface FirebaseApiService {

    @GET("jugadores.json")
    suspend fun getJugadores(): Map<String, JugadorRemoto>

    @GET("partidas.json")
    suspend fun getPartidas(): Map<String, PartidaRemota>

    @GET("premioComun.json")
    suspend fun getPremioComun(): PremioComunRemoto
}