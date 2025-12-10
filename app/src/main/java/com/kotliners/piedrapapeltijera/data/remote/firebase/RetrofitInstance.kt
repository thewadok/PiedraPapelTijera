package com.kotliners.piedrapapeltijera.data.remote.firebase

// Configuración de Retrofit y Moshi
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * Clase de Configuración de Retrofit y Moshi
 * Esta clase sirve para centralizar la URL base de Firebase
 *Moshi con soporte para Kotlin
 * Crea la API real que es FirebaseApiService
 * se usa en FirebaseGameRepository y a traves de cualquir ViewModel que quiera llamar al backend
 */
object RetrofitInstance {

    // URL base de tu base de datos en Firebase
    private const val BASE_URL =
        "https://piedrapapeltijera-bb9b2-default-rtdb.europe-west1.firebasedatabase.app/"

    // Moshi configurado para entender data classes de Kotlin
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Retrofit configurado con la URL base y Moshi como convertidor JSON
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    // Instancia real de la API definida en FirebaseApiService
    val api: FirebaseApiService = retrofit.create(FirebaseApiService::class.java)
}