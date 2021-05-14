package com.phoenix.player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.phoenix.player.model.Queue
import com.phoenix.player.model.Song
import com.phoenix.player.service.PlayerService

class MainActivity : AppCompatActivity() {
    private lateinit var queue: Queue
    private lateinit var playerService: PlayerService

    private fun getSongs(): ArrayList<Song> {
        val songs: ArrayList<Song> = ArrayList()

        songs.add(Song(R.raw.beatles__yesterday, "The Beatles - Yesterday"))
        songs.add(Song(R.raw.eagles__hotel_california, "Eagles - Hotel California"))
        songs.add(Song(R.raw.journey__dont_stop_believin, "Journey - Don't Stop Believin'"))
        songs.add(Song(R.raw.kazood__never_gonna_give_you_up, "Kazoo'd - Never Gonna Give You Up"))
        songs.add(Song(R.raw.red_hot_chili_peppers__californication, "Red Hot Chili Peppers - Californication"))
        songs.add(Song(R.raw.soundtrack__james_bond, "James Bond - Orchestra Soundtracks"))
        songs.add(Song(R.raw.violett_pi__marie_curie, "ViolettPI - Marie Curie"))

//        val musicDir = File("/sdcard/Music")
//        val musicFileExtensions: ArrayList<String> = ArrayList()
//        musicFileExtensions.add(".mp3")
//        musicFileExtensions.add(".m4a")
//        musicFileExtensions.add(".wav")
//        musicFileExtensions.add(".ogg")
//
//        for (file in musicDir.list())
//            println(file)
//        musicDir.listFiles()
////                .filter { it.isFile && musicFileExtensions.contains(it.name.split(".").last().toLowerCase(Locale.ROOT)) }
//                .map { songs.add(Song(it.name, it.name.split(".")[0])) }
//
//        songs.map { println("Test : " + it.fileName) }

        return songs
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listOfSongs = getSongs()

        queue = Queue(listOfSongs, Queue.LoopingMode.LOOP_QUEUE, Queue.ShufflingMode.NO_SHUFFLE)
        playerService = PlayerService(this, queue)
        findViewById<TextView>(R.id.currentlyPlaying).text = queue.currentlyPlayingSong.displayName
        findViewById<TextView>(R.id.totalTimeText).text = playerService.getSongDuration()

        val songsDisplayNames = arrayOfNulls<String>(listOfSongs.size)
        for (i in 0 until listOfSongs.size)
            songsDisplayNames[i] = listOfSongs[i].displayName
        val adapter: ArrayAdapter<*> = ArrayAdapter(this, android.R.layout.simple_list_item_1, songsDisplayNames)
        findViewById<ListView>(R.id.allSongsList).adapter = adapter

        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        seekBar.max = playerService.mediaPlayer.duration / 1000
        seekBar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                            seekBar: SeekBar?,
                            time: Int,
                            fromUser: Boolean
                    ) {
                        println("Current raw time : $time")

                        if (fromUser)
                            playerService.seekTo(time)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        if (seekBar != null)
                            playerService.seekTo(seekBar.progress)
                    }
                }
        )

        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                seekBar.progress = playerService.getSongProgress()
                findViewById<TextView>(R.id.elapsedTimeText).text = playerService.getSongProgressText()
                mainHandler.postDelayed(this, 1000)
            }
        })
    }

    fun toggleIsAudioPlaying(playPauseToggleButton: View) {
        val isPlaying = playerService.togglePause()

        if (!isPlaying)
            playPauseToggleButton.setBackgroundResource(R.drawable.track_play)
        else
            playPauseToggleButton.setBackgroundResource(R.drawable.track_pause)

        findViewById<TextView>(R.id.currentlyPlaying).text = queue.currentlyPlayingSong.displayName
        findViewById<TextView>(R.id.totalTimeText).text = playerService.getSongDuration()
    }

    fun previousAudio(view: View) {
        playerService.rewind()
        findViewById<TextView>(R.id.currentlyPlaying).text = queue.currentlyPlayingSong.displayName
        findViewById<TextView>(R.id.totalTimeText).text = playerService.getSongDuration()

        val isPlaying = playerService.mediaPlayer.isPlaying
        val playPauseToggleButton = findViewById<Button>(R.id.playButton)
        if (!isPlaying)
            playPauseToggleButton.setBackgroundResource(R.drawable.track_play)
        else
            playPauseToggleButton.setBackgroundResource(R.drawable.track_pause)
    }

    fun nextAudio(view: View) {
        playerService.fastForward()
        findViewById<TextView>(R.id.currentlyPlaying).text = queue.currentlyPlayingSong.displayName
        findViewById<TextView>(R.id.totalTimeText).text = playerService.getSongDuration()

        val isPlaying = playerService.mediaPlayer.isPlaying
        val playPauseToggleButton = findViewById<Button>(R.id.playButton)
        if (!isPlaying)
            playPauseToggleButton.setBackgroundResource(R.drawable.track_play)
        else
            playPauseToggleButton.setBackgroundResource(R.drawable.track_pause)
    }

    fun toggleQueueLoop(loopModeButton: View) {
        playerService.cycleLoopModes(queue, playerService.mediaPlayer)
        when (queue.loopingMode) {
            Queue.LoopingMode.LOOP_QUEUE -> loopModeButton.setBackgroundResource(R.drawable.loop_queue)
            Queue.LoopingMode.LOOP_TRACK -> loopModeButton.setBackgroundResource(R.drawable.loop_one_track)
            Queue.LoopingMode.NO_LOOP -> loopModeButton.setBackgroundResource(R.drawable.loop_none)
        }
    }

    fun toggleShuffle(shuffleToggleButton: View) {
        playerService.toggleShuffling(queue)

        when (queue.shufflingMode) {
            Queue.ShufflingMode.SHUFFLE -> shuffleToggleButton.setBackgroundResource(R.drawable.shuffle_true)
            Queue.ShufflingMode.NO_SHUFFLE -> shuffleToggleButton.setBackgroundResource(R.drawable.shuffle_false)
        }
    }
}