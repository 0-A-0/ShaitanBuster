package com.example.scrolllist.data.user_tracks

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface UserTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrack(track: UserTrack)

    @Query("SELECT * FROM user_tracks")
    fun getAllTracks(): Flow<List<UserTrack>>

    @Query("UPDATE user_tracks SET isChosen = 0 ")
    suspend fun deselectAllTracks()

    @Query("UPDATE user_tracks SET isChosen = 1 WHERE id = :trackId")
    suspend fun chooseTrackById(trackId: Int)

    @Query("UPDATE user_tracks SET name = :newName WHERE id = :trackId")
    suspend fun renameTrackById(trackId: Int, newName: String)

    @Query("SELECT * FROM user_tracks WHERE isChosen = 1")
    suspend fun getChosenTrack():UserTrack?

    @Transaction
    suspend fun updateChosenTrackById(trackId: Int) {
        deselectAllTracks()
        chooseTrackById(trackId)
    }

    @Delete
    suspend fun deleteTrack(track: UserTrack)
}