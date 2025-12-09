package com.kotliners.piedrapapeltijera.utils.media

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

/* Esta clase la usamos para guardar capturas de pantalla de nuestro juego en la galería del móvil.
Utilizamos MediaStore porque es la forma moderna y evitamos usar permisos antiguos y peligrosos.
 */
object ScreenshotSaver {

    /* Función que guarda una imagen (Bitmap) en la galería del dispositivo.

   Creamos los datos del archivo.
   Pedimos a Android que cree el archivo mediante MediaStore.
   Escribimos la imagen dentro de ese archivo.
   Liberamos el archivo para que aparezca en la galería.

     */
    suspend fun saveToMediaStore(
        context: Context,
        bitmap: Bitmap,
        fileName: String = "victoria_${System.currentTimeMillis()}.png",
        relativePath: String = android.os.Environment.DIRECTORY_PICTURES + "/Kotliners"
    ) = withContext(Dispatchers.IO) {

        // Averiguamos el tipo de archivo según la extensión.
        val mime = detectImageType(fileName) ?: "image/png"

        // Aquí configuramos los datos que tendrá la imagen en la galería.
        val cv = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, mime)

            // Indicamos donde guardarlo.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver

        // Creamos el archivo en la galería.
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
            ?: return@withContext null

        try {
            // Guardamos la imagen dentro del archivo recién creado.
            resolver.openOutputStream(uri)?.use { out ->
                val format = when (mime.lowercase(Locale.ROOT)) {
                    "image/jpeg", "image/jpg" -> Bitmap.CompressFormat.JPEG
                    "image/webp" -> @Suppress("DEPRECATION") Bitmap.CompressFormat.WEBP
                    else -> Bitmap.CompressFormat.PNG
                }

                // Guardamos la imagen con buena calidad.
                bitmap.compress(format, 100, out)
            }

            // Avisamos al sistema que terminamos de guardar.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val done = ContentValues().apply {
                    put(MediaStore.Images.Media.IS_PENDING, 0)
                }
                resolver.update(uri, done, null, null)
            }

            // Devolvemos la ruta del archivo por si queremos usarla después.
            uri

        } catch (e: Exception) {
            // Si algo sale mal, eliminamos el archivo para no dejar basura en la galería.
            e.printStackTrace()
            resolver.delete(uri, null, null)
            null
        }
    }

    // Detectamos el tipo del archivo según su extensión.
    private fun detectImageType(name: String): String? {
        val lower = name.lowercase(Locale.ROOT)
        return when {
            lower.endsWith(".png") -> "image/png"
            lower.endsWith(".jpg") || lower.endsWith(".jpeg") -> "image/jpeg"
            lower.endsWith(".webp") -> "image/webp"
            else -> null
        }
    }
}





