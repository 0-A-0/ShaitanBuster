package com.example.scrolllist.ui.screens.audio_lobby

import android.app.Application
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.scrolllist.data.database.AppDatabase
import com.example.scrolllist.data.user_tracks.UserTrack
import com.example.scrolllist.managers.AudioManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AudioLobbyViewModel(application: Application) : AndroidViewModel(application) {

    private val userTrackDao = AppDatabase.getInstance(application).userTrackDao()
//    private val scope = Dispatchers.IO

    val tracks = userTrackDao.getAllTracks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun chooseTrack(trackId: Int) {
        viewModelScope.launch {
            userTrackDao.updateChosenTrackById(trackId)
        }
    }

    fun deleteTrack(track: UserTrack) {
        viewModelScope.launch {
            userTrackDao.deleteTrack(track)
        }
    }

    fun setDefaultTrack() {
        viewModelScope.launch {
            userTrackDao.deselectAllTracks()
        }
    }

    fun addTrack(uri: Uri) {
        viewModelScope.launch {
            try {
                application.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                var name = ""
                var duration = ""
                val retriever = MediaMetadataRetriever()
                try {
                    retriever.setDataSource(application, uri)
                    name = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                        ?: "anonymous"
                    duration =
                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toString()
                } finally {
                    retriever.release()
                }
                val newTrack = UserTrack(
                    name = name,
                    duration = duration.toLongOrNull()?:0L,
                    uri = uri.toString(),
                )
                userTrackDao.addTrack(newTrack)
            } catch (e: Exception) {
                println("audio load error")
            }
        }
    }
}