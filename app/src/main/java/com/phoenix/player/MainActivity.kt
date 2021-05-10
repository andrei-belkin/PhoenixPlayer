package com.phoenix.player

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.SeekBar

class MainActivity : AppCompatActivity() {
    private val queue: List<Int> = listOf(
            R.raw.pushnoy___alisa,
            R.raw.bi2___ee_glaza,
            R.raw.bi2___hipster,
            R.raw.bi2___polkovniku_nikto_ne_pishet,
            R.raw.bi2___pora_vozvrashhatysya_domoy,
            R.raw.ddt___v_poslednyuyu_oseny
    )
    private var loopingMode: LoopingMode = LoopingMode.LOOP_QUEUE
    private var isShuffling: Boolean = false
    private var currentTrack: Int = queue.first()
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var runnable: Runnable

    enum class LoopingMode {
        LOOP_QUEUE,
        LOOP_TRACK,
        NO_LOOP
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaPlayer = MediaPlayer.create(this, currentTrack)

        initSongsListView(queue)

        val arrayAdapter: ArrayAdapter<*> =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, queue)
        val allSongsList = findViewById<ListView>(R.id.all_songs_list)
        allSongsList.adapter = arrayAdapter

        val loopModeButton = findViewById<Button>(R.id.queueLoopButton)
        when (loopingMode) {
            LoopingMode.LOOP_QUEUE -> loopModeButton.setBackgroundResource(R.drawable.loop_queue)
            LoopingMode.LOOP_TRACK -> loopModeButton.setBackgroundResource(R.drawable.loop_one_track)
            LoopingMode.NO_LOOP -> loopModeButton.setBackgroundResource(R.drawable.loop_none)
        }

        val shuffleToggleButton = findViewById<Button>(R.id.queueShuffleButton)
        if (isShuffling)
            shuffleToggleButton.setBackgroundResource(R.drawable.shuffle_true)
        else
            shuffleToggleButton.setBackgroundResource(R.drawable.shuffle_false)

        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        seekBar.max = mediaPlayer.duration.div(1000)
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
                            seekBar.progress = mediaPlayer.currentPosition / 1000
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        if (seekBar != null)
                            seekBar.progress = mediaPlayer.currentPosition
                    }
                }
        )

        runnable = Runnable {
            while (true) {
                seekBar.progress = mediaPlayer.currentPosition
                Thread.sleep(500)
                println("Updated seekbar : " + seekBar.progress)
            }
        }

        Thread(runnable)

        mediaPlayer.setOnPreparedListener {
            mediaPlayer.setNextMediaPlayer(MediaPlayer.create(this, getNextAudio(currentTrack)))
        }
    }

    fun initSongsListView(queue: List<Int>) {
        R.raw.
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

    fun previousAudio(view: View) {
        if (mediaPlayer.currentPosition.toDouble() / mediaPlayer.duration.toDouble() < 0.05)
            mediaPlayer.selectTrack(0)
        else
            mediaPlayer.seekTo(0)

        println("Current time : " + mediaPlayer.currentPosition.toDouble())
        println("Max time : " + mediaPlayer.duration.toDouble())
    }

    fun nextAudio(view: View) {
        mediaPlayer.seekTo(mediaPlayer.duration)
    }

    fun toggleQueueLoop(loopModeButton: View) {
        val currentModeIndex = LoopingMode.valueOf(loopingMode.name).ordinal
        val nextModeIndex = (currentModeIndex + 1) % (LoopingMode.values().size)

        loopingMode = LoopingMode.values()[nextModeIndex]

        mediaPlayer.isLooping = loopingMode == LoopingMode.LOOP_TRACK
        if (loopingMode == LoopingMode.NO_LOOP)
            mediaPlayer.setOnCompletionListener {
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
            }

        when (loopingMode) {
            LoopingMode.LOOP_QUEUE -> loopModeButton.setBackgroundResource(R.drawable.loop_queue)
            LoopingMode.LOOP_TRACK -> loopModeButton.setBackgroundResource(R.drawable.loop_one_track)
            LoopingMode.NO_LOOP -> loopModeButton.setBackgroundResource(R.drawable.loop_none)
        }
    }

    fun toggleShuffle(shuffleToggleButton: View) {
        isShuffling = !isShuffling
        shuffleToggleButton.setBackgroundResource(if (isShuffling) R.drawable.shuffle_true else R.drawable.shuffle_false)
    }

    private fun getNextAudio(currentTrack: Int): Int {
        val currentTrackIndex = queue.indexOf(currentTrack)
        val nextTrackIndex = (currentTrackIndex + 1) % (queue.size - 1)

        return queue[nextTrackIndex]
    }
}