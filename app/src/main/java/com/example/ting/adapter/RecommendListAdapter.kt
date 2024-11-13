package com.example.ting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import coil3.request.transformations
import coil3.transform.RoundedCornersTransformation
import com.example.ting.R
import com.example.ting.databinding.ItemRecommendBinding
import com.example.ting.model.Album
import com.example.ting.other.convertNumber
import com.example.ting.other.string

class RecommendListAdapter : PagingDataAdapter<Album, RecommendListAdapter.RecommendListViewHolder>(
    object : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean =
            oldItem == newItem
    }
) {
    inner class RecommendListViewHolder(val binding: ItemRecommendBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: RecommendListViewHolder, position: Int) {
        getItem(position)?.let {
            with(holder.binding) {
                albumCover.load(it.coverUrl) {
                    crossfade(1000)
                    placeholder(R.color.white)
                    transformations(RoundedCornersTransformation(20f))
                }
                albumTitleTv.text = it.albumTitle
                albumDescriptionTv.text = it.albumIntro
                albumPlayCount.text = it.playCount.convertNumber()
                albumContentSize.text = R.string.content_size.string(it.includeTrackCount)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendListViewHolder =
        RecommendListViewHolder(ItemRecommendBinding.inflate(LayoutInflater.from(parent.context), parent, false))
}