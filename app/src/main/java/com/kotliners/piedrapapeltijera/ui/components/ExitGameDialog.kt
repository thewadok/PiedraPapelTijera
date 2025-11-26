package com.kotliners.piedrapapeltijera.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotliners.piedrapapeltijera.ui.theme.RosaNeon

@Composable
fun ExitGameDialog(
    onConfirmExit: () -> Unit,
    onDismiss: () -> Unit
) {
    //Dialogo para salir del juego
    AlertDialog(
        onDismissRequest = onDismiss,

        containerColor = RosaNeon,

        // Título
        title = {
            TituloSeccion(
                text = "Salir del juego",
                modifier = Modifier.padding(bottom = 4.dp)
            )
        },

        text = {
            Parrafo(
                texto = "¿Seguro que quieres salir de la aplicación?",
                modifier = Modifier.padding(bottom = 4.dp)
            )
        },

        // Botón Salir
        confirmButton = {
            NeonTextoBoton("Salir") { onConfirmExit() }
        },

        // Botón Cancelar
        dismissButton = {
            NeonTextoBoton("Cancelar") { onDismiss() }
        }
    )
}