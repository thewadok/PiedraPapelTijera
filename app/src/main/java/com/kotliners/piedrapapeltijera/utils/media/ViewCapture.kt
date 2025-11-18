package com.kotliners.piedrapapeltijera.utils.media

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.drawToBitmap

/*
Función que nos permite capturar la vista actual que se muestra en pantalla dentro de Jetpack Compose
y lo convierte en una imagen Bitmap.
 */
@Composable
fun rememberCaptureCurrentView(): () -> Bitmap {
    // Obtenemos la vista actual que Cómpose esta mostrando.
    val view = LocalView.current

    // Devolvemos otra función que al ejecutarse dibuja la vista actual en un Bitmap usando drawToBitmap.
    return remember(view) { { view.drawToBitmap(Bitmap.Config.ARGB_8888) } }
}