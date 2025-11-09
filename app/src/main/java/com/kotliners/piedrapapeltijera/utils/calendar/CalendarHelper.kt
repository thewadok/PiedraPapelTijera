package com.kotliners.piedrapapeltijera.utils.calendar

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.TimeZone

/* Creamos un helper para añadir eventos al calendario del móvil.
Usamos CalendarContract, que es la aplicación de Android para trabajar con el calendario.
 */
object CalendarHelper {
    // Función para insertar un evento en el calendario.
    suspend fun insertVictoryEvent(
        context: Context,
        title: String = "Victoria en el juego",
        description: String = "¡He ganado una partida!",
        startMillis: Long = System.currentTimeMillis(),
        durationMinutes: Int = 15
    ): Long? = withContext(Dispatchers.IO) {

        // Primero, buscamos el calendario que esté visible para escribir el evento ahí
        val calId = getFirstVisibleCalendarId(context.contentResolver) ?: return@withContext null

        /* Calculamos la hora de final del evento, 15 minutos por defecto,
        para que genere un bloque normal en el calendario, facil de ver.
         */
        val endMillis = startMillis + durationMinutes * 60_000L

        // Creamos todos los valores del evento
        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, startMillis)
            put(CalendarContract.Events.DTEND, endMillis)
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.DESCRIPTION, description)
            put(CalendarContract.Events.CALENDAR_ID, calId)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        // Insertamos el evento en el calendario del sistema
        val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            ?: return@withContext null

        // Devolvemos el ID del evento para poder usarlo
        ContentUris.parseId(uri)
    }

    // Función para buscar el primer calendario visible del dispositivo
    private fun getFirstVisibleCalendarId(resolver: ContentResolver): Long? {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.VISIBLE
        )

        resolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            "${CalendarContract.Calendars.VISIBLE}=1",
            null,
            null
        ).use { cursor ->
            if (cursor == null) return null
            return if (cursor.moveToFirst()) cursor.getLong(0) else null
        }
    }
}