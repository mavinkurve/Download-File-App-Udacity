package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityDetailBinding
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityDetailBinding>(this, R.layout.activity_detail)
        setSupportActionBar(toolbar)


        val downloadedRepo = intent.getStringExtra(applicationContext.getString(R.string.file_name_key))
        Log.i("Detail_Activity",downloadedRepo.orEmpty())
        download_fileName.text = downloadedRepo
        val downloadStatus = intent.getStringExtra(applicationContext.getString(R.string.status_key))
        Log.i("Detail_Activity",downloadStatus.orEmpty())
        setDownloadStatus(downloadStatus)

        binding.contentDetail.closeButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancelNotification()
    }

    private fun setDownloadStatus(downloadStatus: String?) = when (downloadStatus) {
        applicationContext.getString(R.string.status_success) -> {
            download_status.text = getString(R.string.success_text)
            download_status.setTextColor(ContextCompat.getColor(applicationContext, R.color.green))
        }
        applicationContext.getString(R.string.status_fail) -> {
            download_status.text = getString(R.string.fail_text)
            download_status.setTextColor(ContextCompat.getColor(applicationContext, R.color.red))
        }
        else -> {
            download_status.text = getString(R.string.unavailable_text)
            download_status.setTextColor(Color.GRAY)
        }
    }

}
