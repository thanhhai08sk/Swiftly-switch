package org.de_studio.recentappswitcher

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.view.View

fun View.onClick(func: () -> Unit) {
    setOnClickListener { func.invoke() }
}

fun createChannelIfHavent(context: Context, channelId: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(
                NotificationChannel(
                        channelId,
                        "Default",
                        NotificationManager.IMPORTANCE_MIN
                )
        )
    }
}
