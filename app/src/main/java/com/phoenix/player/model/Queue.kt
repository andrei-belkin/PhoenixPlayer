package com.phoenix.player.model

data class Queue(val songsList: ArrayList<Song>, var loopingMode: LoopingMode, var shufflingMode: ShufflingMode) {
    var currentlyPlayingSong: Song = songsList[0]

    enum class LoopingMode {
        LOOP_QUEUE,
        LOOP_TRACK,
        NO_LOOP
    }

    enum class ShufflingMode {
        SHUFFLE,
        NO_SHUFFLE
    }
}