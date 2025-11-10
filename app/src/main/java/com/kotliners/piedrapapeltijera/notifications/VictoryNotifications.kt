package com.kotliners.piedrapapeltijera.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.kotliners.piedrapapeltijera.MainActivity
import com.kotliners.piedrapapeltijera.R
import android.graphics.BitmapFactory

object VictoryNotification {

    private const val CHANNEL_ID = "victory_channel"
    private const val CHANNEL_NAME = "Victorias"
    private const val CHANNEL_DESC = "Notificaciones al ganar partidas"
    private const val NOTIFICATION_ID = 1001

    fun show(context: Context) {
        // En Android 13+ hay que comprobar el permiso en tiempo de ejecución
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                // Sin permiso, salimos silenciosamente
                return
            }
        }

        // 🔹 Crear el canal si no existe
        createChannel(context)

        // 🔹 Intent para abrir la app al pulsar la notificación
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        // Cargar la imagen grande desde drawable
        val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logo_prov_kotliners)

        // 🔹 Construcción de la notificación
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // sigue usando el icono pequeño de siempre
            .setContentTitle(context.getString(R.string.victory_title))
            .setContentText(context.getString(R.string.victory_text))
            .setStyle(
                NotificationCompat.BigPictureStyle() // 👇 aquí añadimos el logo grande
                    .bigPicture(logoBitmap)
                    .bigLargeIcon(null as android.graphics.Bitmap?)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // 🔹 Enviar la notificación con control de seguridad
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (se: SecurityException) {
            se.printStackTrace()
        }
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESC
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}