package com.kotliners.piedrapapeltijera.utils.calendar

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.TimeZone
import android.util.Log

private const val TAG = "CalendarHelper"

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
        try {
            Log.d(TAG, "Insertando evento de victoria...")

            // Primero, buscamos el calendario que esté visible para escribir el evento ahí
            val calId = getFirstVisibleCalendarId(context.contentResolver)
            Log.d(TAG, "calId encontrado = $calId")

            if (calId == null) {
                Log.e(TAG, "No se ha encontrado ningún calendario visible")
                return@withContext null
            }

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
            if (uri == null) {
                Log.e(TAG, "insert() devolvió null, no se creó el evento")
                return@withContext null
            }

            // Devolvemos el ID del evento para poder usarlo
            val id = ContentUris.parseId(uri)
            Log.d(TAG, "Evento creado correctamente con id = $id")
            id
        } catch (e: SecurityException) {
            Log.e(TAG, "Sin permisos para escribir en el calendario", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error insertando evento en calendario", e)
            null
        }
    }

    // Obtenemos el nombre de la cuenta principal a partir de los nombres de los calendarios visibles.
    private fun getPrimaryAccountName(resolver: ContentResolver): String? {
        val projection = arrayOf(
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.VISIBLE
        )

        resolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            "${CalendarContract.Calendars.VISIBLE} = 1",
            null,
            null
        ).use { cursor ->
            if (cursor == null) return null

            val accounts = mutableSetOf<String>()

            while (cursor.moveToNext()) {
                val account = cursor.getString(0) ?: continue
                accounts.add(account)
            }

            val first = accounts.firstOrNull()
            Log.d(TAG, "Cuentas visibles encontradas: $accounts, usando: $first")
            return first
        }
    }

    // Obtenida la cuenta principal, buscamos un calendario visible cuyo nombre coincida con el de la cuenta.
    private fun getCalendarIdForPrimaryAccount(
        resolver: ContentResolver,
        primaryAccount: String
    ): Long? {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.VISIBLE
        )

        resolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            "${CalendarContract.Calendars.VISIBLE} = 1",
            null,
            null
        ).use { cursor ->
            if (cursor == null) return null

            while (cursor.moveToNext()) {
                val id = cursor.getLong(0)
                val name = cursor.getString(1) ?: ""

                Log.d(TAG, "Revisando calendario: id=$id name=$name visible=1")

                if (name == primaryAccount) {
                    Log.d(TAG, "Calendario coincidente con cuenta principal encontrado: id=$id")
                    return id
                }
            }
        }

        return null
    }

    // Función para buscar el primer calendario visible del dispositivo
    private fun getFirstVisibleCalendarId(resolver: ContentResolver): Long? {
        // Obtenemos el nombre de la cuenta principal
        val primaryAccount = getPrimaryAccountName(resolver)
        if (primaryAccount == null) {
            Log.e(TAG, "No se pudo determinar la cuenta principal del usuario")
            return null
        }
        Log.d(TAG, "Cuenta principal detectada: $primaryAccount")

        // Buscamos un calendario visible cuyo nombre sea igual a esa cuenta.
        val calendarId = getCalendarIdForPrimaryAccount(resolver, primaryAccount)
        if (calendarId != null) {
            Log.d(TAG, "Usando calendario principal con id=$calendarId")
            return calendarId
        }

        Log.e(TAG, "No se encontró un calendario visible para la cuenta principal")
        return null
    }
}