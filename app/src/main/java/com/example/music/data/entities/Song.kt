package com.example.music.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/*
 * Created by 无聊r丶 on 2021/9/13.
 * Copyright (c) 2021 All rights reserved.
 */

@Parcelize
data class Song(
    val mediaId: String = "",
    val title: String = "",
    val subtitle: String = "",
    val songUrl: String = "",
    val imageUrl: String = ""
) : Parcelable