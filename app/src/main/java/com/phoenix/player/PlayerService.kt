package com.phoenix.player

import android.content.Context
import android.media.MediaPlayer

/**
 * Is used to control everything that has to deal with audio playback. Queue, play modes, loop modes, etc.
 */
data class PlayerService(
        val context: Context,
        val queue: List<Int> = ArrayList(),
        val playerLoopingMode: PlayerLoopingMode = PlayerLoopingMode.LOOP_QUEUE
) {
    private var mediaPlayer: MediaPlayer = MediaPlayer.create(context, queue.first())
    private var totalQueueDuration = mediaPlayer.duration

    init {
        mediaPlayer.setVolume(.5f, .5f)
        mediaPlayer.setNextMediaPlayer(MediaPlayer.create(context, ))
    }

    enum class PlayerLoopingMode {
        NO_LOOP,
        LOOP_TRACK,
        LOOP_QUEUE
    }
}