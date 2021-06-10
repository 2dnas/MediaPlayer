package com.example.mediaplayer

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import java.io.File

class SongsManager(context: Context) {
    val MEDIA_PATH = "/sdcard/Music"
    val songList = ArrayList<HashMap<String,String>>()

    fun getAllSong() : ArrayList<HashMap<String,String>>{
        val home = File(MEDIA_PATH)

        for (file in home.listFiles()){
            val song = HashMap<String,String>()
            song.put("Song Title", file.name)
            song.put("Song Path",file.path)
            songList.add(song)
        }
        return songList
    }



}