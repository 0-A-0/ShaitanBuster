package com.example.scrolllist.managers

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.scrolllist.R

object AudioManager {
    private var soundPool: SoundPool? = null
    private var musicPlayer: MediaPlayer? = null
    private val sounds = mutableMapOf<SoundType, Int>()
    private var loadedSoundsCount = 0
    var _soundVolume: Float by mutableStateOf(1f)
        private set
    var _musicVolume: Float by mutableStateOf(0.3f)
        private set

    enum class SoundType(val volume:Float) { AXE_HIT(1f), REVOLVER_SHOT(1f), SHOTGUN_SHOT(0.3f), PELLETS_CRIBE(1f), CARTRIDGES_CRIBE(1f),HOLY_MOMENT(1f) }

    fun init(context: Context) {
        if (soundPool != null || musicPlayer != null) return

        musicPlayer = MediaPlayer.create(context, R.raw.best_soundtrack).apply {
            isLooping = true
            setVolume(_musicVolume, _musicVolume)
        }

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(attributes)
            .build()

        soundPool?.let { pool ->
            sounds[SoundType.AXE_HIT] = pool.load(context, R.raw.axe_hit, 1)
            sounds[SoundType.SHOTGUN_SHOT] = pool.load(context, R.raw.shotgun_shot, 1)
            sounds[SoundType.REVOLVER_SHOT] = pool.load(context, R.raw.revolver_shot, 1)
            sounds[SoundType.PELLETS_CRIBE] = pool.load(context, R.raw.pellets_cribe, 1)
            sounds[SoundType.CARTRIDGES_CRIBE] = pool.load(context, R.raw.cartridges_cribe, 1)
            sounds[SoundType.HOLY_MOMENT] = pool.load(context, R.raw.holy_moment, 1)

            pool.setOnLoadCompleteListener { _, _, _ ->
                loadedSoundsCount++
            }
        }
    }

    fun play(type: SoundType) {
        sounds[type]?.let { id ->
            soundPool?.play(id, type.volume * _soundVolume, type.volume * _soundVolume, 1, 0, 1f)
        }
    }
    fun playMusic(isPaused: Boolean){
        if (!isPaused) musicPlayer?.start() else musicPlayer?.pause()
    }
    fun stopMusic(){
        musicPlayer?.pause()
        musicPlayer?.seekTo(0)
    }
    fun setMusicSpeed(speed:Float){
        musicPlayer?.let {
            it.playbackParams =
                it.playbackParams.setSpeed(speed)
        }
    }
    fun setSoundVolume(volume:Float){
        _soundVolume = volume
    }
    fun setMusicVolume(volume:Float){
        _musicVolume = volume
        musicPlayer?.setVolume(_musicVolume, _musicVolume)
    }

    fun release() {
        musicPlayer?.stop()
        musicPlayer?.release()
        musicPlayer = null
        soundPool?.release()
        soundPool = null
        sounds.clear()
    }
}
