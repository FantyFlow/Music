package com.example.music.exoplayer

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import com.example.music.data.remote.MusicDatabase
import com.example.music.exoplayer.State.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import javax.inject.Inject

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}

class FirebaseMusicSource @Inject constructor(
    private val musicDatabase: MusicDatabase
) {
    var songs = emptyList<MediaMetadataCompat>()
    suspend fun fetchMediaData() {
        state = STATE_INITIALIZING
        val allSongs = musicDatabase.getAllSongs()
        songs = allSongs.map {
            Builder().putString(METADATA_KEY_MEDIA_ID, it.mediaId)
                .putString(METADATA_KEY_TITLE, it.title)
                .putString(METADATA_KEY_DISPLAY_TITLE, it.title)
                .putString(METADATA_KEY_ARTIST, it.subtitle)
                .putString(METADATA_KEY_DISPLAY_SUBTITLE, it.subtitle)
                .putString(METADATA_KEY_DISPLAY_ICON_URI, it.imageUrl)
                .putString(METADATA_KEY_MEDIA_URI, it.songUrl)
                .putString(METADATA_KEY_ALBUM_ART_URI, it.imageUrl)
                .putString(METADATA_KEY_DISPLAY_DESCRIPTION, it.subtitle)
                .build()
        }
        state = STATE_INITIALIZED
    }

    fun asMediaSource(dataSourceFactory: DefaultDataSource.Factory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach {
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(it.getString(METADATA_KEY_MEDIA_URI)))
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItems() = songs.map {
        val desc = MediaDescriptionCompat.Builder()
            .setMediaId(it.description.mediaId)
            .setTitle(it.description.title)
            .setSubtitle(it.description.subtitle)
            .setMediaUri(it.description.mediaUri)
            .setIconUri(it.description.iconUri)
            .build()
        MediaBrowserCompat.MediaItem(desc, FLAG_PLAYABLE)
    }.toMutableList()

    //检测音乐是否下载完成
    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()
    private var state = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach {
                        it(state == STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }

    fun whenReady(action: (Boolean) -> Unit): Boolean =
        if (state == STATE_CREATED || state == STATE_INITIALIZING) {
            onReadyListeners += action
            false
        } else {
            action(state == STATE_INITIALIZED)
            true
        }
}