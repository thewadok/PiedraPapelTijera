package com.kotliners.piedrapapeltijera.data.remote.firebase

// Configuración de Retrofit y Moshi
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

// Objeto singleton que crea una sola instancia de Retrofit
object RetrofitInstance {

    // URL base de tu base de datos de Firebase
    private const val BASE_URL =
        "https://piedrapapeltijera-bb9b2-default-rtdb.europe-west1.firebasedatabase.app/"

    // Moshi convierte JSON a objetos Kotlin
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Retrofit se encarga de hacer las peticiones a Firebase
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    // Accedemos a las funciones de la API
    val api: FirebaseApiService = retrofit.create(FirebaseApiService::class.java)
}