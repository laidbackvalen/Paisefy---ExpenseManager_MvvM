package com.example.paisefy_moneymanager.receiver.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.paisefy_moneymanager.R
import com.example.paisefy_moneymanager.views.activities.MainActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("TITLE") ?: "Todo Reminder"

        // Get the default notification sound URI
        val alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        // Create an intent to launch your main activity when the notification is clicked
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "todo_channel"

        // Create a notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Todo Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText("Check reminder for more!")
            .setSmallIcon(R.drawable.baseline_currency_rupee_24)
            .setContentIntent(pendingIntent)
            .setSound(alarmSoundUri) // Set the notification sound
            .setAutoCancel(true)
            .build()

        notificationManager.notify(0, notification)
    }
}