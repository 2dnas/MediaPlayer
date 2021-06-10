package com.example.mediaplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build


const val CHANNEL1 = "Channel1"
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel1 = NotificationChannel(
                CHANNEL1,
                "PlayingChannel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel1)
        }
    }
}