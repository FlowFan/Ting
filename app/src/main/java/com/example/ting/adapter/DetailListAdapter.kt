package com.example.ting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.ting.R
import com.example.ting.databinding.ItemDetailBinding
import com.example.ting.model.Detail
import com.example.ting.other.convertNumber
import com.example.ting.other.string

class DetailListAdapter : PagingDataAdapter<Detail.Track, DetailListAdapter.DetailListViewHolder>(
    object : DiffUtil.ItemCallback<Detail.Track>() {
        override fun areItemsTheSame(oldItem: Detail.Track, newItem: Detail.Track): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Detail.Track, newItem: Detail.Track): Boolean =
            oldItem == newItem
    }
) {
    inner class DetailListViewHolder(val binding: ItemDetailBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: DetailListViewHolder, position: Int) {
        getItem(position)?.let {
            with(holder.binding) {
                albumCover.load(it.coverUrl) {
                    crossfade(1000)
                    placeholder(R.color.white)
                    transformations(RoundedCornersTransformation(20f))
                }
                albumTitleTv.text = it.trackTitle
                albumPlayCount.text = R.string.play_count.string(it.playCount.convertNumber())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailListViewHolder =
        DetailListViewHolder(ItemDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false))
}