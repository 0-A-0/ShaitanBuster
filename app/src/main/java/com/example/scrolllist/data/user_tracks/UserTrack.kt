package com.example.scrolllist.data.user_tracks

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_tracks")
data class UserTrack(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val uri: String,
    val duration: Long,
    val isChosen: Boolean = false
)
