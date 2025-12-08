package com.kotliners.piedrapapeltijera.data.remote.firebase

// Configuración de Retrofit y Moshi

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object RetrofitInstance {

    // TU URL BASE (la que ves arriba en Realtime Database) + barra final
    private const val BASE_URL =
        "https://piedrapapeltijera-bb9b2-default-rtdb.europe-west1.firebasedatabase.app/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()


    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: FirebaseApiService = retrofit.create(FirebaseApiService::class.java)
}
