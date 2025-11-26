package com.kotliners.piedrapapeltijera.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Dialogo que aparece en el momento de la victoria
@Composable
fun VictoryDialog(
    onConfirm: (saveScreenshot: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var save by remember { mutableStateOf(true) }

    // Dialogo de victoria.
    AlertDialog(

        // Si el usuario toca fuera del dialogo lo cerramos igual.
        onDismissRequest = onDismiss,

        // Titulo.
        title = { Text("¡Victoria!") },

        // Cuerpo.
        text = {
            Column {
                Row(Modifier.padding(vertical = 4.dp)) {

                    // Checkbox para activar o desactivar
                    Checkbox(
                        checked = save,
                        onCheckedChange = { save = it }
                    )
                    Spacer(Modifier.width(8.dp))

                    Text("Guardar captura")
                }
            }
        },

        // Boton aceptar
        confirmButton = {
            TextButton(onClick = { onConfirm(save) }) {
                Text("Aceptar")
            }
        },

        // Boton cancelar
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}