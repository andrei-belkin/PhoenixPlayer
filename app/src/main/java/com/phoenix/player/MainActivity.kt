package com.phoenix.player

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar

class MainActivity : AppCompatActivity() {
    private var player: PlayerService = PlayerService(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val volumeBar = findViewById<SeekBar>(R.id.volumeBar)
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        val systemAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        totalTime = mediaPlayer.duration

        volumeBar.max = systemAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        volumeBar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                            seekBar: SeekBar?,
                            volume: Int,
                            fromUser: Boolean
                    ) {
                        println("Current raw volume : $volume")
                        println("Current system volume : " + systemAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
                        println("Max system volume : " + systemAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))

                        if (fromUser)
                            systemAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        if (seekBar != null)
                            seekBar.progress = systemAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                }
        )

        seekBar.max = mediaPlayer.duration
        seekBar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                            seekBar: SeekBar?,
                            time: Int,
                            fromUser: Boolean
                    ) {
                        println("Current raw time : $time")

                        if (fromUser)
                            mediaPlayer.seekTo(time)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        if (seekBar != null)
                            seekBar.progress = mediaPlayer.currentPosition
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                }
        )
    }

    fun toggleIsAudioPlaying(playPauseToggleButton: View) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            playPauseToggleButton.setBackgroundResource(R.drawable.track_play)
        } else {
            mediaPlayer.start()
            playPauseToggleButton.setBackgroundResource(R.drawable.track_pause)
        }
    }

    fun previousAudio(view: View) {}
    fun nextAudio(view: View) {}

    fun toggleQueueLoop(view: View) {
        mediaPlayer.isLooping = !mediaPlayer.isLooping
    }
}