package com.kotliners.piedrapapeltijera.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.kotliners.piedrapapeltijera.ui.theme.FondoNegro
import com.kotliners.piedrapapeltijera.utils.LocaleManager

// 🌐 Pantalla de Ayuda que carga el HTML correcto según el idioma seleccionado
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HelpScreen() {
    val context = LocalContext.current
    val currentLang = LocaleManager.getSavedLanguage(context) ?: "es"

    // Determina qué archivo HTML cargar según el idioma
    val htmlFile = when (currentLang) {
        "en" -> "help_en.html"
        else -> "help_es.html"
    }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                webViewClient = WebViewClient() // Abre dentro de la app
                settings.javaScriptEnabled = true
                setBackgroundColor(android.graphics.Color.parseColor("#1F1F1F"))
                loadUrl("file:///android_asset/$htmlFile")
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(FondoNegro)
    )
}