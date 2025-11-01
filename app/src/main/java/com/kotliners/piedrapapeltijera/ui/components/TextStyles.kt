package com.kotliners.piedrapapeltijera.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import com.kotliners.piedrapapeltijera.ui.theme.AmarilloNeon
import com.kotliners.piedrapapeltijera.ui.theme.TextoBlanco

@Composable
fun TituloSeccion(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = AmarilloNeon,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun Parrafo(texto: String, modifier: Modifier = Modifier) {
    Text(
        texto,
        modifier = modifier.fillMaxWidth(),
        color = TextoBlanco,
        fontSize = 16.sp,
        lineHeight = 20.sp
    )
}

@Composable
fun TextoLinea(titulo: String, detalle: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text("$titulo ", color = AmarilloNeon, fontWeight = FontWeight.Bold)
        Text(detalle, color = TextoBlanco)
    }
}

