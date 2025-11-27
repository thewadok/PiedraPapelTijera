package com.kotliners.piedrapapeltijera.utils.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationManager(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentLocation(): Location? {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            // Si no hay permiso, no podemos hacer nada.
            // La solicitud de permiso se gestionará en la UI.
            return null
        }

        // Usamos una corrutina para esperar el resultado asíncrono de la API de ubicación.
        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { location: Location? ->
                // Cuando la ubicación se obtiene con éxito, reanudamos la corrutina con el resultado.
                if (continuation.isActive) {
                    continuation.resume(location)
                }
            }.addOnFailureListener { _ ->
                // En caso de fallo, reanudamos con null.
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
        }
    }
}