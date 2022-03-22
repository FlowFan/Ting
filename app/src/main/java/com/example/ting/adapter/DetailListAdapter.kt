package com.example.ting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ting.databinding.ItemDetailBinding
import com.ximalaya.ting.android.opensdk.model.track.Track

class DetailListAdapter : PagingDataAdapter<Track, DetailListAdapter.DetailListViewHolder>(
    object : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track) =
            oldItem.dataId == newItem.dataId

        override fun areContentsTheSame(oldItem: Track, newItem: Track) = oldItem == newItem
    }) {
    inner class DetailListViewHolder(val binding: ItemDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DetailListViewHolder(
            ItemDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(
        holder: DetailListViewHolder,
        position: Int
    ) {
        getItem(position)?.let {
            holder.binding.track = it
        }
    }
}