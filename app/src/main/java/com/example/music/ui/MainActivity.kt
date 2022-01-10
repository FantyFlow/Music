package com.example.music.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.RequestManager
import com.example.music.adapters.SwipeSongAdapter
import com.example.music.data.entities.Song
import com.example.music.databinding.ActivityMainBinding
import com.example.music.other.Status.*
import com.example.music.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel>()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    @Inject
    lateinit var glide: RequestManager

    private var curPlayingSong: Song? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.vpSong.adapter = swipeSongAdapter

        mainViewModel.mediaItems.observe(this) {
            when (it.status) {
                SUCCESS -> {
                    swipeSongAdapter.submitList(it.data)
                    glide.load(curPlayingSong?.imageUrl ?: it.data?.get(0)?.imageUrl)
                        .into(binding.ivCurSongImage)
                    if (curPlayingSong == null) {
                        return@observe
                    } else {
                        val newItemIndex = swipeSongAdapter.currentList.indexOf(curPlayingSong)
                        if (newItemIndex != -1) {
                            binding.vpSong.currentItem = newItemIndex
                        }
                    }
                }
                ERROR -> Unit
                LOADING -> Unit
            }
        }
        mainViewModel.curPlayingSong.observe(this) {
            if (it == null) return@observe
            with(it.description) {
                curPlayingSong = Song(
                    mediaId.toString(),
                    title.toString(),
                    subtitle.toString(),
                    mediaUri.toString(),
                    iconUri.toString()
                )
            }
            glide.load(curPlayingSong?.imageUrl).into(binding.ivCurSongImage)
            if (curPlayingSong == null) {
                return@observe
            } else {
                val newItemIndex = swipeSongAdapter.currentList.indexOf(curPlayingSong)
                if (newItemIndex != -1) {
                    binding.vpSong.currentItem = newItemIndex
                }
            }
        }
    }
}