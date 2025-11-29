package com.kotliners.piedrapapeltijera.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.kotliners.piedrapapeltijera.MainActivity
import com.kotliners.piedrapapeltijera.R
import java.util.Locale

object VictoryNotification {

    private const val CHANNEL_ID = "victory_channel"
    private const val NOTIFICATION_ID = 1001

    // Muestramos una notificación de victoria con logo grande.
    fun show(context: Context, durationMs: Long) {
        // Comprobamos permisos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        // Creamos el canal si hace falta
        createChannel(context)

        // Formateamos tiempo mínimo de 0.1 segundos
        val seconds = if (durationMs < 100) 0.1 else durationMs / 1000.0

        // Texto de la notificación según idioma
        val notificationText = context.getString(
            R.string.notif_victory_text,
            seconds
        )

        val formattedTime = String.format(Locale.getDefault(), "%.1f", seconds)

        // Comportamiento al tocar la notificación
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("EXTRA_TIME", formattedTime)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Logo grande del proyecto
        val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logo_prov_kotliners)

        // Construcción de la notificación
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(logoBitmap)
            .setContentTitle(context.getString(R.string.notif_victory_title))
            .setContentText(notificationText)
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(logoBitmap)
                    .bigLargeIcon(null as android.graphics.Bitmap?)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            // Ignoramos si no hay permiso
        }
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notif_victory_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notif_victory_channel_desc)
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}