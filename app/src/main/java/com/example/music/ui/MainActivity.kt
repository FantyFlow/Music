package com.example.music.ui

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.example.music.R
import com.example.music.adapters.SwipeSongAdapter
import com.example.music.data.entities.Song
import com.example.music.databinding.ActivityMainBinding
import com.example.music.exoplayer.isPlaying
import com.example.music.other.Status.*
import com.example.music.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel>()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val navController by lazy { binding.fragmentContainerView.getFragment<NavHostFragment>().navController }

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    private var curPlayingSong: Song? = null

    private var playbackState: PlaybackStateCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.vpSong.adapter = swipeSongAdapter
        binding.vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (playbackState?.isPlaying == true) {
                    mainViewModel.playOrToggleSong(swipeSongAdapter.currentList[position])
                } else {
                    curPlayingSong = swipeSongAdapter.currentList[position]
                }
            }
        })
        mainViewModel.mediaItems.observe(this) {
            when (it.status) {
                SUCCESS -> {
                    swipeSongAdapter.submitList(it.data)
                    binding.ivCurSongImage.load(
                        curPlayingSong?.imageUrl ?: it.data?.firstOrNull()?.imageUrl
                    )
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
            binding.ivCurSongImage.load(curPlayingSong?.imageUrl)
            if (curPlayingSong == null) {
                return@observe
            } else {
                val newItemIndex = swipeSongAdapter.currentList.indexOf(curPlayingSong)
                if (newItemIndex != -1) {
                    binding.vpSong.currentItem = newItemIndex
                }
            }
        }
        mainViewModel.playbackState.observe(this) {
            playbackState = it
            binding.ivPlayPause.setImageResource(
                if (playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
        }
        mainViewModel.isConnected.observe(this) {
            when (it.getContentIfNotHandled()?.status) {
                ERROR -> Toast.makeText(this, "An unknown error occurred", Toast.LENGTH_LONG).show()
                else -> Unit
            }
        }
        mainViewModel.networkError.observe(this) {
            when (it.getContentIfNotHandled()?.status) {
                ERROR -> Toast.makeText(this, "An unknown error occurred", Toast.LENGTH_LONG).show()
                else -> Unit
            }
        }
        binding.ivPlayPause.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }
        swipeSongAdapter.setItemClickListener {
            navController.navigate(R.id.action_homeFragment_to_songFragment)
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomBar.isVisible = destination.id != R.id.songFragment
        }
    }
}