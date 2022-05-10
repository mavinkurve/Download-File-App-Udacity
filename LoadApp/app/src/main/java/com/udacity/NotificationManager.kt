package com.udacity
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.MainActivity
import com.udacity.R


private const val NOTIFICATION_ID = 0
private const val REQUEST_CODE = 0
private const val FLAGS = 0

fun NotificationManager.sendNotification(repo: Repository, applicationContext: Context, pendingIntent: PendingIntent) {

    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        // Set title, text, and icon builder
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(repo.name)
        .setContentText(repo.description)

        // Set content intent
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

        // Set action
        .addAction(
            R.drawable.ic_baseline_cloud_download_24,
            applicationContext.getString(R.string.notification_button),
            pendingIntent)

        // Set priority
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    // Deliver the notification
    notify(NOTIFICATION_ID, builder.build())
}

// Cancel all notification
fun NotificationManager.cancelNotification() {
    cancelAll()
}