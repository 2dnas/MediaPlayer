package com.example.mediaplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.core.net.toUri

class NotificationMediaController : BroadcastReceiver()  {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(mediaPlayer!!.isPlaying){
            mediaPlayer!!.pause()
        } else{
            mediaPlayer!!.start()
        }
    }
}

class NotificationMediaControllerNext() : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(mediaPlayer!!.isPlaying){
            currentSong++
            if(currentSong >= songs.size){
                playNextSong(context, songs[0]["Uri"]?.toUri()!!)
                currentSong = 0

            }else{
                playNextSong(context, songs[currentSong]["Uri"]?.toUri()!!)
            }
        }
    }


}


class NotificationMediaControllerPrevious : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(mediaPlayer!!.isPlaying){
            currentSong--
            if(currentSong < 0){
                currentSong = songs.size - 1
                playPreviousSong(context,songs[currentSong]["Uri"]?.toUri()!!)

            }else{
                playPreviousSong(context,songs[currentSong]["Uri"]?.toUri()!!)
            }
        }
    }


}

fun playNextSong(context: Context?, uri : Uri) {
    mediaPlayer!!.stop()
    mediaPlayer = null
    mediaPlayer = MediaPlayer.create(context,uri)
    mediaPlayer!!.start()
}

fun playPreviousSong(context: Context?,uri : Uri) {
    mediaPlayer!!.stop()
    mediaPlayer = null
    mediaPlayer = MediaPlayer.create(context,uri)
    mediaPlayer!!.start()
}