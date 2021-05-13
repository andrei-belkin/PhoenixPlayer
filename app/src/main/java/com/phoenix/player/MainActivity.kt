package com.phoenix.player

import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.phoenix.player.model.Queue
import com.phoenix.player.model.Song
import java.io.File

class MainActivity : AppCompatActivity() {
    private val queue: List<Int> = listOf(
            R.raw.pushnoy___alisa,
            R.raw.bi2___ee_glaza,
            R.raw.bi2___hipster,
            R.raw.bi2___polkovniku_nikto_ne_pishet,
            R.raw.bi2___pora_vozvrashhatysya_domoy,
            R.raw.ddt___v_poslednyuyu_oseny
    )
    private var loopingMode: Queue.LoopingMode = Queue.LoopingMode.LOOP_QUEUE
    private var isShuffling: Boolean = false
    private var currentTrack: Int = queue.first()
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var runnable: Runnable

    private val songs: ArrayList<Song> = ArrayList()

    private fun loadSongs() {
        songs.add(Song("test1.mp3", "Майданов", "Кто такие русские", 140))
        songs.add(Song("test2.mp3", "Пушной", "Красная Шапочка", 184))
        songs.add(Song("test3.mp3", "Трофим", "Вне закона", 160))
        songs.add(Song("test4.mp3", "Кино", "Спокойная ночь", 170))
        songs.add(Song("test5.mp3", "Кино", "Звезда по имени Солнце", 140))
        songs.add(Song("test6.mp3", "Кино", "Группа крови", 106))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadSongs()

        println(MediaStore.Audio.Media.TITLE)


        mediaPlayer = MediaPlayer.create(this, currentTrack)

        initSongsListView(queue)

        val arrayAdapter: ArrayAdapter<*> =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, queue)
        val allSongsList = findViewById<ListView>(R.id.all_songs_list)
        allSongsList.adapter = arrayAdapter

        val loopModeButton = findViewById<Button>(R.id.queueLoopButton)
        when (loopingMode) {
            Queue.LoopingMode.LOOP_QUEUE -> loopModeButton.setBackgroundResource(R.drawable.loop_queue)
            Queue.LoopingMode.LOOP_TRACK -> loopModeButton.setBackgroundResource(R.drawable.loop_one_track)
            Queue.LoopingMode.NO_LOOP -> loopModeButton.setBackgroundResource(R.drawable.loop_none)
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
        val currentModeIndex = Queue.LoopingMode.valueOf(loopingMode.name).ordinal
        val nextModeIndex = (currentModeIndex + 1) % (Queue.LoopingMode.values().size)

        loopingMode = Queue.LoopingMode.values()[nextModeIndex]

        mediaPlayer.isLooping = loopingMode == Queue.LoopingMode.LOOP_TRACK
        if (loopingMode == Queue.LoopingMode.NO_LOOP)
            mediaPlayer.setOnCompletionListener {
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
            }

        when (loopingMode) {
            Queue.LoopingMode.LOOP_QUEUE -> loopModeButton.setBackgroundResource(R.drawable.loop_queue)
            Queue.LoopingMode.LOOP_TRACK -> loopModeButton.setBackgroundResource(R.drawable.loop_one_track)
            Queue.LoopingMode.NO_LOOP -> loopModeButton.setBackgroundResource(R.drawable.loop_none)
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