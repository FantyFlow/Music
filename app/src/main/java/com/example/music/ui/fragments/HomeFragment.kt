package com.example.music.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.music.adapters.SongAdapter
import com.example.music.databinding.FragmentHomeBinding
import com.example.music.other.Status.*
import com.example.music.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel by activityViewModels<MainViewModel>()

    @Inject
    lateinit var songAdapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvAllSongs.adapter = songAdapter
        mainViewModel.mediaItems.observe(viewLifecycleOwner) {
            when (it.status) {
                SUCCESS -> {
                    binding.allSongsProgressBar.isVisible = false
                    songAdapter.submitList(it.data)
                }
                ERROR -> Unit
                LOADING -> Unit
            }
        }
        songAdapter.setItemClickListener {
            mainViewModel.playOrToggleSong(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}