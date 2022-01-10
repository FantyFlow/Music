package com.example.music.adapters

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.music.data.entities.Song

abstract class BaseSongAdapter : ListAdapter<Song, BaseSongAdapter.SongViewHolder>(
    object : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song) =
            oldItem.mediaId == newItem.mediaId

        override fun areContentsTheSame(oldItem: Song, newItem: Song) = oldItem == newItem
    }) {
    class SongViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    protected var onItemClickListener: ((Song) -> Unit)? = null
    fun setItemClickListener(listener: (Song) -> Unit) {
        onItemClickListener = listener
    }
}