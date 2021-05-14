package com.phoenix.player

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.phoenix.player.model.Queue
import com.phoenix.player.model.Song
import com.phoenix.player.service.PlayerService
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var queue: Queue
    private lateinit var playerService: PlayerService

    private fun getSongs(): ArrayList<Song> {
        val songs: ArrayList<Song> = ArrayList()

        songs.add(Song(R.raw.bi2___ee_glaza, "Би-2 - Её глаза"))
        songs.add(Song(R.raw.bi2___pora_vozvrashhatysya_domoy, "Би-2 - Пора возвращаться домой"))
        songs.add(Song(R.raw.bi2___hipster, "Би-2 - Хипстер"))
        songs.add(Song(R.raw.pushnoy___ne_plachy_obo_mne, "Пушной - Не плачь обо мне"))
        songs.add(Song(R.raw.voskresenie___kto_vinovat, "Воскресенье - Кто виноват"))

//        val musicDir = File("/sdcard/Music")
//        val musicFileExtensions: ArrayList<String> = ArrayList()
//        musicFileExtensions.add(".mp3")
//        musicFileExtensions.add(".m4a")
//        musicFileExtensions.add(".wav")
//        musicFileExtensions.add(".ogg")

//        for (file in musicDir.list())
//            println(file)
//        musicDir.listFiles()
////                .filter { it.isFile && musicFileExtensions.contains(it.name.split(".").last().toLowerCase(Locale.ROOT)) }
//                .map { songs.add(Song(it.name, it.name.split(".")[0])) }

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
        seekBar.max = playerService.mediaPlayer.duration.div(1000)
        seekBar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                            seekBar: SeekBar?,
                            time: Int,
                            fromUser: Boolean
                    ) {
                        println("Current raw time : $time")

                        if (fromUser)
                            playerService.mediaPlayer.seekTo(time)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        if (seekBar != null)
                            seekBar.progress = playerService.mediaPlayer.currentPosition / 1000
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        if (seekBar != null)
                            seekBar.progress = playerService.mediaPlayer.currentPosition
                    }
                }
        )
    }

    fun toggleIsAudioPlaying(playPauseToggleButton: View) {
        val isPlaying = playerService.togglePause()

        if (isPlaying)
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
    }

    fun nextAudio(view: View) {
        playerService.fastForward()
        findViewById<TextView>(R.id.currentlyPlaying).text = queue.currentlyPlayingSong.displayName
        findViewById<TextView>(R.id.totalTimeText).text = playerService.getSongDuration()
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