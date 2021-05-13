package com.phoenix.player.service

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.phoenix.player.model.Queue
import com.phoenix.player.model.Song
import java.io.File

class PlayerService(
        queue: Queue,
        context: Context
) {
    var mediaPlayer: MediaPlayer = MediaPlayer.create(context, Uri.fromFile(File(queue.currentlyPlayingSong.fileName)))

    init {
        mediaPlayer.setOnCompletionListener {
            val nextSong: Song = QueueService.getNextTrack(queue) ?: return@setOnCompletionListener
            mediaPlayer = MediaPlayer.create(context, Uri.fromFile(File(nextSong.fileName)))
        }
    }
}