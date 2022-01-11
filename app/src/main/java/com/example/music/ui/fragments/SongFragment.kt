package com.example.music.ui.fragments

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.RequestManager
import com.example.music.R
import com.example.music.data.entities.Song
import com.example.music.databinding.FragmentSongBinding
import com.example.music.exoplayer.isPlaying
import com.example.music.other.Status.SUCCESS
import com.example.music.ui.viewmodels.MainViewModel
import com.example.music.ui.viewmodels.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : Fragment() {
    private var _binding: FragmentSongBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val songViewModel by viewModels<SongViewModel>()

    @Inject
    lateinit var glide: RequestManager

    private var curPlayingSong: Song? = null

    private var playbackState: PlaybackStateCompat? = null

    private var shouldUpdateSeekbar = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.mediaItems.observe(viewLifecycleOwner) {
            when (it.status) {
                SUCCESS -> {
                    if (curPlayingSong == null && it.data?.isNotEmpty() == true) {
                        curPlayingSong = it.data[0]
                        updateTitleAndSongImage(it.data[0])
                    }
                }
                else -> Unit
            }
        }
        mainViewModel.curPlayingSong.observe(viewLifecycleOwner) {
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
            updateTitleAndSongImage(curPlayingSong!!)
        }
        mainViewModel.playbackState.observe(viewLifecycleOwner) {
            playbackState = it
            binding.ivPlayPauseDetail.setImageResource(
                if (playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
            binding.seekBar.progress = it?.position?.toInt() ?: 0
        }

        val dataFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        songViewModel.curPlayerPosition.observe(viewLifecycleOwner) {
            if (shouldUpdateSeekbar) {
                binding.seekBar.progress = it.toInt()
                binding.tvCurTime.text = dataFormat.format(it)
            }
        }
        songViewModel.curSongDuration.observe(viewLifecycleOwner) {
            Log.d("Hello", "onViewCreated: $it")
            binding.seekBar.max = it.toInt()
            binding.tvSongDuration.text = dataFormat.format(it)
        }
        binding.ivPlayPauseDetail.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    binding.tvCurTime.text = dataFormat.format(p1.toLong())
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                shouldUpdateSeekbar = false
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                p0?.let {
                    mainViewModel.seekTo(it.progress.toLong())
                }
                shouldUpdateSeekbar = true
            }
        })
        binding.ivSkipPrevious.setOnClickListener {
            mainViewModel.skipToPreviousSong()
        }
        binding.ivSkip.setOnClickListener {
            mainViewModel.skipToNextSong()
        }
    }

    private fun updateTitleAndSongImage(song: Song) {
        val text = "${song.title} - ${song.subtitle}"
        binding.tvSongName.text = text
        glide.load(song.imageUrl).into(binding.ivSongImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}