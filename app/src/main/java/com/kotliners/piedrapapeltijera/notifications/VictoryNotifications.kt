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

object VictoryNotification {

    private const val CHANNEL_ID = "victory_channel"
    private const val CHANNEL_NAME = "Victorias"
    private const val CHANNEL_DESC = "Notificaciones al ganar partidas"
    private const val NOTIFICATION_ID = 1001

    /**
     * Muestra una notificación de victoria con logo grande (BigPicture).
     */
    fun show(context: Context, durationMs: Long) {
        // En Android 13+ comprobar permiso
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        createChannel(context)

        // 🕒 Formatear tiempo mínimo de 0.1 segundos
        val seconds = if (durationMs < 100) 0.1 else durationMs / 1000.0
        val formattedTime = String.format("%.1f segundos", seconds)

        // Intent al tocar la notificación
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
            .setSmallIcon(R.mipmap.ic_launcher) // 👈 usa el icono de app por defecto
            .setLargeIcon(logoBitmap)
            .setContentTitle("🏆 ¡Victoria!")
            .setContentText("Resolviste la partida en $formattedTime ⏱")
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
            // Evita crash si no hay permiso
        }
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}