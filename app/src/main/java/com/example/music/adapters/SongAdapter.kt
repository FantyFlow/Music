package com.example.music.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import coil.load
import com.example.music.databinding.ListItemBinding
import javax.inject.Inject

class SongAdapter @Inject constructor() : BaseSongAdapter() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val holder = SongViewHolder(
            ListItemBinding.inflate(
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
        (holder.binding as ListItemBinding).apply {
            tvPrimary.text = song.title
            tvSecondary.text = song.subtitle
            ivItemImage.load(song.imageUrl)
        }
    }
}