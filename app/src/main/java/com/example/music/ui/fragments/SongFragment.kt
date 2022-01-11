package com.example.music.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.RequestManager
import com.example.music.data.entities.Song
import com.example.music.databinding.FragmentSongBinding
import com.example.music.other.Status.SUCCESS
import com.example.music.ui.viewmodels.MainViewModel
import com.example.music.ui.viewmodels.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
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