package com.example.music.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.music.databinding.SwipeItemBinding
import javax.inject.Inject

class SwipeSongAdapter @Inject constructor() : BaseSongAdapter() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val holder = SongViewHolder(
            SwipeItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        holder.itemView.setOnClickListener {
            onItemClickListener?.let {
                it(getItem(holder.absoluteAdapterPosition))
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position)
        val text = "${song.title} - ${song.subtitle}"
        (holder.binding as SwipeItemBinding).tvPrimary.text = text
    }
}