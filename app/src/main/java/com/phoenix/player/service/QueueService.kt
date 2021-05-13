package com.phoenix.player.service

import com.phoenix.player.model.Queue
import com.phoenix.player.model.Queue.LoopingMode
import com.phoenix.player.model.Song

abstract class QueueService {
    companion object {
        @JvmStatic
        fun getNextTrack(queue: Queue): Song? {
            when (queue.loopingMode) {
                LoopingMode.LOOP_QUEUE -> return queue.songsList[(queue.songsList.indexOf(queue.currentlyPlayingSong) + 1) % (queue.songsList.size - 1)]
                LoopingMode.LOOP_TRACK -> return queue.currentlyPlayingSong
                LoopingMode.NO_LOOP -> {
                    val nextIndex = queue.songsList.indexOf(queue.currentlyPlayingSong) + 1
                    if (nextIndex > queue.songsList.size - 1)
                        return null
                    return queue.songsList[nextIndex]
                }
            }
        }
    }
}