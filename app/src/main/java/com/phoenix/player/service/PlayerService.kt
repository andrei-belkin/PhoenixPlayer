package com.phoenix.player.service

import android.content.Context
import android.media.MediaPlayer
import com.phoenix.player.model.Queue
import com.phoenix.player.model.Song
import kotlin.random.Random

class PlayerService(private val context: Context, private val queue: Queue) {
    var mediaPlayer: MediaPlayer = MediaPlayer.create(context, queue.currentlyPlayingSong.fileId)

    init {
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.release()
            val nextSong: Song = getNextTrack(queue)
            mediaPlayer = MediaPlayer.create(context, nextSong.fileId)
        }
    }

    fun togglePause(): Boolean {
        if (mediaPlayer.isPlaying)
            mediaPlayer.pause()
        else
            mediaPlayer.start()

        return mediaPlayer.isPlaying
    }

    fun rewind() {
        if (mediaPlayer.currentPosition < 5000) {
            queue.currentlyPlayingSong = getPreviousTrack(queue)
            mediaPlayer.stop()
            mediaPlayer.release()
            mediaPlayer = MediaPlayer.create(context, queue.currentlyPlayingSong.fileId)
            mediaPlayer.start()
        } else
            mediaPlayer.seekTo(0)
    }

    fun fastForward() {
        mediaPlayer.stop()
        mediaPlayer.release()
        queue.currentlyPlayingSong = getNextTrack(queue)
        mediaPlayer = MediaPlayer.create(context, queue.currentlyPlayingSong.fileId)
        mediaPlayer.start()
    }

    private fun getNextTrack(queue: Queue): Song {
        if (queue.shufflingMode == Queue.ShufflingMode.SHUFFLE)
            return queue.songsList[Random.nextInt(0, queue.songsList.size)]
        when (queue.loopingMode) {
            Queue.LoopingMode.LOOP_QUEUE -> return queue.songsList[(queue.songsList.indexOf(queue.currentlyPlayingSong) + 1) % (queue.songsList.size)]
            Queue.LoopingMode.LOOP_TRACK -> return queue.currentlyPlayingSong
            Queue.LoopingMode.NO_LOOP -> {
                val nextIndex = queue.songsList.indexOf(queue.currentlyPlayingSong) + 1
                if (nextIndex > queue.songsList.size - 1)
                    return queue.currentlyPlayingSong
                return queue.songsList[nextIndex]
            }
        }
    }

    private fun getPreviousTrack(queue: Queue): Song {
        val currentIndex: Int = queue.songsList.indexOf(queue.currentlyPlayingSong)

        when (queue.loopingMode) {
            Queue.LoopingMode.LOOP_QUEUE -> {
                if (currentIndex == 0)
                    return queue.songsList.last()
                return queue.songsList[(currentIndex - 1) % (queue.songsList.size - 1)]
            }
            Queue.LoopingMode.LOOP_TRACK -> return queue.currentlyPlayingSong
            Queue.LoopingMode.NO_LOOP -> {
                val nextIndex = currentIndex + 1
                if (nextIndex > queue.songsList.size - 1)
                    return queue.currentlyPlayingSong
                return queue.songsList[nextIndex]
            }
        }
    }

    fun cycleLoopModes(queue: Queue, mediaPlayer: MediaPlayer) {
        val currentModeIndex = Queue.LoopingMode.valueOf(queue.loopingMode.name).ordinal
        val nextModeIndex = (currentModeIndex + 1) % (Queue.LoopingMode.values().size)

        queue.loopingMode = Queue.LoopingMode.values()[nextModeIndex]

        mediaPlayer.isLooping = queue.loopingMode == Queue.LoopingMode.LOOP_TRACK
        if (queue.loopingMode == Queue.LoopingMode.NO_LOOP)
            mediaPlayer.setOnCompletionListener {
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
            }
    }

    fun toggleShuffling(queue: Queue) {
        queue.shufflingMode = Queue.ShufflingMode.values()[(queue.shufflingMode.ordinal + 1) % Queue.ShufflingMode.values().size]
    }

    fun getSongDuration(): String {
        val songDuration = mediaPlayer.duration / 1000
        var minutes = (songDuration % 60).toString()
        if (minutes.length == 1)
            minutes = "0$minutes"
        return String.format("%s:%s", songDuration / 60, minutes)
    }

    fun getSongProgress(): Int = mediaPlayer.currentPosition / 1000

    fun getSongProgressText(): String {
        val songProgress = mediaPlayer.currentPosition / 1000
        var minutes = (songProgress % 60).toString()
        if (minutes.length == 1)
            minutes = "0$minutes"
        return String.format("%s:%s", songProgress / 60, minutes)
    }

    fun seekTo(time: Int) {
        mediaPlayer.seekTo(time * 1000)
    }
}