package com.example.ximalaya.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.ximalaya.R
import com.example.ximalaya.databinding.ItemRecommendBinding
import com.ximalaya.ting.android.opensdk.model.album.Album

class RecommendListAdapter :
    PagingDataAdapter<Album, RecommendListViewHolder>(object : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Album, newItem: Album) = oldItem == newItem
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendListViewHolder {
        val holder = RecommendListViewHolder(
            ItemRecommendBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        return holder
    }

    override fun onBindViewHolder(holder: RecommendListViewHolder, position: Int) {
        val album = getItem(position)
        holder.binding.album = album
        holder.binding.coverUrl = album?.coverUrlLarge
    }

    companion object {
        @JvmStatic
        @BindingAdapter("image")
        fun setImage(imageView: ImageView, url: String) {
            if (url.isNotEmpty()) {
                imageView.load(url) {
                    placeholder(R.drawable.ic_launcher_background)
                    transformations(RoundedCornersTransformation(20f))
                }
            }
        }
    }
}

class RecommendListViewHolder(val binding: ItemRecommendBinding) :
    RecyclerView.ViewHolder(binding.root)