package com.example.mediaplayer

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.widget.SeekBar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.example.mediaplayer.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

var mediaPlayer: MediaPlayer? = null
var songs = ArrayList<HashMap<String,String>>()
var currentSong = 0

val SECONDS = 1000
class MainActivity : AppCompatActivity() {
    private lateinit var notificationManagerCompat : NotificationManagerCompat
    private var coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
    private lateinit var binding : ActivityMainBinding
    var isPlaying = false
    private var currentPosition = 0
    private var fullTime = 0
    private var hasTouch = false
    private var currentSong = 0
    private lateinit var mediaSession : MediaSessionCompat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        val projection = arrayOf( MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION)



        val cursor = this.managedQuery(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null);

        while(cursor.moveToNext()){
            val hashMap = HashMap<String,String>()
            hashMap["Title"] = cursor.getString(2)
            hashMap["Uri"] = cursor.getString(3)
            songs.add(hashMap)
        }

        songs.forEach { it.forEach { Log.d("songs",it.key + it.value) } }

        notificationManagerCompat = NotificationManagerCompat.from(this)
        mediaSession = MediaSessionCompat(this,"tag")



        mediaPlayer = MediaPlayer.create(this, songs[currentSong]["Uri"]?.toUri())


        binding.previous.setOnClickListener {
            currentSong--
            if(currentSong < 0){
                currentSong = songs.size - 1
                playPreviousSong(songs[currentSong]["Uri"]?.toUri()!!)

            }else{
                playPreviousSong(songs[currentSong]["Uri"]?.toUri()!!)
            }
        }
        binding.next.setOnClickListener {
            currentSong++
            if(currentSong >= songs.size){
                playNextSong(songs[0]["Uri"]?.toUri()!!)
                currentSong = 0

            }else{
                playNextSong(songs[currentSong]["Uri"]?.toUri()!!)
            }
        }


        binding.play.setOnClickListener {
            if(isPlaying){
                isPlaying = false
                mediaPlayer!!.pause()
                binding.play.setImageResource(R.drawable.ic_baseline_play_arrow)
            }else{
                coroutineScope.launch {
                    mediaPlayer!!.start()
                }
                fullTime = mediaPlayer!!.duration / SECONDS
                binding.seekbar.max = fullTime
                isPlaying = true
                binding.play.setImageResource(R.drawable.ic_baseline_pause)
                coroutineScope.launch {
                    while (isPlaying){
                        delay(1000)
                        binding.seekbar.progress = mediaPlayer!!.currentPosition / SECONDS
                    }
                }
                binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if(hasTouch)
                        mediaPlayer!!.seekTo(progress * 1000)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        hasTouch = true
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        hasTouch = false
                    }

                })
            }

        }

    }

    override fun onStop() {
        super.onStop()
        showStatusBarController()
    }

    private fun showStatusBarController(){
        val notificationMediaController = NotificationMediaController()
        val intent = Intent(this,notificationMediaController::class.java)
        val intentNext = Intent(this,NotificationMediaControllerNext::class.java)
        val pendingIntentNext = PendingIntent.getBroadcast(this,0,intentNext,PendingIntent.FLAG_UPDATE_CURRENT)
        val intentPrevious = Intent(this,NotificationMediaControllerPrevious::class.java)
        val pendingIntentPrevious = PendingIntent.getBroadcast(this,0,intentPrevious,PendingIntent.FLAG_UPDATE_CURRENT)
        val pendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        val notification  = NotificationCompat.Builder(this, CHANNEL1)
            .setSmallIcon(R.drawable.coldplay)
            .setLargeIcon(bitmapFactory())
            .setContentTitle("MediaPlayer")
            .setContentText("Avengers")
            .addAction(R.drawable.ic_baseline_skip_previous,"Previous",pendingIntentPrevious)
            .addAction(R.drawable.ic_baseline_play_arrow,"Play",pendingIntent)
            .addAction(R.drawable.ic_baseline_skip_next,"Next",pendingIntentNext)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0,1,2)
                .setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationManager.IMPORTANCE_LOW)
            .build()
        notificationManagerCompat.notify(1,notification)
    }

    private fun bitmapFactory() : Bitmap{
        return BitmapFactory.decodeResource(resources,R.drawable.avengers_endgame)
    }

    fun playNextSong(uri : Uri) {
        mediaPlayer!!.stop()
        mediaPlayer = null
        mediaPlayer = MediaPlayer.create(this,uri)
        mediaPlayer!!.start()
    }

    fun playPreviousSong(uri : Uri) {
        mediaPlayer!!.stop()
        mediaPlayer = null
        mediaPlayer = MediaPlayer.create(this,uri)
        mediaPlayer!!.start()
    }


}
