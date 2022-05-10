package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var repo : Repository
    private var status = ""
    private var downloadStatus: Int? = null
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        createChannel()

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        download_button.setOnClickListener {

            when (radio_group.checkedRadioButtonId) {
                R.id.glide_option -> {
                    Log.i("Radio", "Glide")
                    repo = Repository(applicationContext.getString(R.string.glide_title),
                        applicationContext.getString(R.string.glide_notification_description),
                        applicationContext.getString(R.string.glide_repo))
                }
                R.id.udacity_option -> {
                    repo = Repository(applicationContext.getString(R.string.udacity_title),
                        applicationContext.getString(R.string.udacity_notification_description),
                        applicationContext.getString(R.string.udacity_repo))
                }
                R.id.retrofit_option -> {
                    Log.i("Radio", "Retrofit")
                    repo = Repository(applicationContext.getString(R.string.retrofit_title),
                        applicationContext.getString(R.string.retrofit_notification_description),
                        applicationContext.getString(R.string.retrofit_repo))
                }
                else -> {
                    Log.i("Radio", "None")
                }
            }
            if (::repo.isInitialized) {
                download_button.onButtonStateChanged(ButtonState.Loading)
                download(repo.url)
            }
            else {
                download_button.onButtonStateChanged(ButtonState.Clicked)
                Toast.makeText(applicationContext,
                    applicationContext.getText(R.string.invalid_download),
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("Receiver", "received")
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (downloadID == id) {

                val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val query = id?.let {
                    DownloadManager.Query().setFilterById(it)
                }
                val cursor = downloadManager.query(query)

                if (cursor.moveToFirst()) {
                    downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                }

                status = when (downloadStatus) {
                    DownloadManager.STATUS_SUCCESSFUL ->
                        applicationContext.getString(R.string.status_success)
                    DownloadManager.STATUS_FAILED ->
                        applicationContext.getString(R.string.status_fail)
                    else ->
                        applicationContext.getString(R.string.status_unavailable)
                }

                val contentIntent = Intent(applicationContext, DetailActivity::class.java)
                contentIntent.putExtra(applicationContext.getString(R.string.file_name_key), repo.name)
                contentIntent.putExtra(applicationContext.getString(R.string.status_key), status)

                // Create pending intent
                val pendingIntent = PendingIntent.getActivity(
                    applicationContext,
                    downloadID.toInt(),
                    contentIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

                notificationManager.sendNotification(
                    repo,
                    applicationContext,
                    pendingIntent)

                download_button.onButtonStateChanged(ButtonState.Completed)
            }
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(getString(R.string.notification_channel_id),
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH)
            channel.description = applicationContext.getString(R.string.glide_notification_description)
            notificationManager = this.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun download(url: String) {
        Log.i("Download:", "downloading.....")
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}