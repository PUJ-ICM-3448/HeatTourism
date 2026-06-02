package com.naranjapina.heat_tourism.shared.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.naranjapina.heat_tourism.R

object NotificationCenter {
    const val CHAT_CHANNEL_ID = "heat_tourism_chat_channel"
    private const val CHAT_CHANNEL_NAME = "HeatTourism Notificaciones"
    private const val CHAT_CHANNEL_DESCRIPTION = "Mensajes, solicitudes y alertas"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHAT_CHANNEL_ID,
            CHAT_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHAT_CHANNEL_DESCRIPTION
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    fun show(context: Context, title: String, body: String) {
        val notification = NotificationCompat.Builder(context, CHAT_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}

