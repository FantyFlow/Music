package com.example.music.data.remote

import com.example.music.data.entities.Song
import com.example.music.other.Constants.SONG_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/*
 * Created by 无聊r丶 on 2021/9/13.
 * Copyright (c) 2021 All rights reserved.
 */

class MusicDatabase {
    private val db = FirebaseFirestore.getInstance()
    private val songCollection = db.collection(SONG_COLLECTION)

    suspend fun getAllSongs(): List<Song> = withContext(Dispatchers.IO) {
        try {
            songCollection.get().await().toObjects(Song::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}