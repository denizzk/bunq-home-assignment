package com.dkarakaya.core.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build

class CreateNotification(
    private val context: Context,
    private val icon: Int,
    private val title: String,
    private val content: String
) {

    private var notificationManager: NotificationManager = context.getSystemService(
        Context.NOTIFICATION_SERVICE
    ) as NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder

    operator fun invoke() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                CHANNEL_ID,
                content,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(icon)
        } else {
            builder = Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(icon)
        }
        notificationManager.notify(1234, builder.build())
    }

    companion object {
        const val CHANNEL_ID = "com.dkarakaya.bunqhomeassignment"
    }
}
