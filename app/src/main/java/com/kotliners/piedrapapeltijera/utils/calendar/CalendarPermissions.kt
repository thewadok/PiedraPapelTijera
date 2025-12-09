package com.kotliners.piedrapapeltijera.utils.calendar

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

// Gestionamos los permisos del calendario en tiempo real.
@Composable
fun rememberCalendarPermissionState(): Pair<() -> Unit, State<Boolean>> {

    val context = LocalContext.current

    // Estado que indica si los permisos están concedidos
    var granted by remember { mutableStateOf(false) }

    // Comprobamos si tenemos los dos permisos necesarios
    fun check(): Boolean {
        val readOk = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED

        val writeOk = ContextCompat.checkSelfPermission(
            context, Manifest.permission.WRITE_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED

        return readOk && writeOk
    }

    // Comprobamos permisos  al entrar en el compose
    LaunchedEffect(Unit) {
        granted = check()
    }

    // Muestramos el cuadro de diálogo de permisos de Android
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        // Si todos los permisos han sido aceptados → granted = true
        granted = results.values.all { it }
    }

    // Función para pedir permisos cuando lo necesitamos
    val request = {
        if (!check()) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR
                )
            )
        } else {
            granted = true
        }
    }

    val grantedState = remember { derivedStateOf { granted } }

    return request to grantedState
}